package com.example.service;

import com.example.dtos.TaskDto;
import com.example.dtos.TaskRequest;
import com.example.entity.Task;
import com.example.entity.User;
import com.example.exceptions.tasks.TaskNotFoundException;
import com.example.exceptions.tasks.UnauthorizedOperationException;
import com.example.repository.TaskRepository;
import com.example.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;


@ApplicationScoped
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final Logger logger;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, Logger logger) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.logger = logger;
    }

    public List<TaskDto> getAllTasks(String username) {
        // Implementation to retrieve all tasks
        List<Task> tasks=taskRepository.findByUser(username);
        List<TaskDto> taskDtos = new ArrayList<>();
        for (Task task : tasks) {
            taskDtos.add(new TaskDto(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(),task.getPriority(),task.getCreatedAt(),task.getUpdatedAt()));
        }
        return taskDtos;
    }

    @Transactional
    public TaskDto createTask(TaskRequest taskDto, String subject) {

        User user = userRepository.findByUsername(subject);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Task task = new Task();

        task.setTitle(taskDto.title());
        task.setDescription(taskDto.description());
        task.setStatus(taskDto.status());
        task.setPriority(taskDto.priority());
        task.setUser(user);

        taskRepository.persist(task);

        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    //check whether task belong to the person or not
    @Transactional
    public TaskDto updateTask( TaskRequest taskRequest, Long id, String username) {
        Task task=taskRepository.findByUsernameAndId(username,id);
        if(task==null){
            logger.error("Task not found for user: " + username + " with task id: " + id);
            throw new TaskNotFoundException("Task not found for user: " + username + " with task id: " + id);
        }
        task.setTitle(taskRequest.title());
        task.setDescription(taskRequest.description());
        task.setStatus(taskRequest.status());
        task.setPriority(taskRequest.priority());
        taskRepository.flush();


        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    public TaskDto getTaskDetails( Long id, String subject) {
        Task task=taskRepository.findById(id);
        if (task==null){
            logger.error("Task not found for user: " + subject + " with task id: " + id);
            throw new TaskNotFoundException("Task not found for user: " + subject + " with task id: " + id);
        }else if (task.getUser() == null || !task.getUser().getUsername().equals(subject)) {
            logger.log(Logger.Level.INFO, "User: " + subject + " is not authorized to access task with id: " + id);
            throw new UnauthorizedOperationException("User: " + subject + " is not authorized to access task with id: " + id);
        }
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    @Transactional
    public void deleteTask(Long id, String subject) {
        Task task=taskRepository.findById(id);
        if (task==null){
            logger.log(Logger.Level.INFO, "Task not found for user: " + subject + " with task id: " + id);
            throw new TaskNotFoundException("Task not found for user: " + subject + " with task id: " + id);
        } else if (task.getUser() == null || !task.getUser().getUsername().equals(subject)) {
            logger.log(Logger.Level.INFO, "User: " + subject + " is not authorized to delete task with id: " + id);
            throw new UnauthorizedOperationException("User: " + subject + " is not authorized to delete task with id: " + id);
        }
        taskRepository.delete(task);
    }
}

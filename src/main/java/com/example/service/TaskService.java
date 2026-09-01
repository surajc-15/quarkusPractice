package com.example.service;

import com.example.dtos.TaskDto;
import com.example.dtos.TaskRequest;
import com.example.entity.Task;
import com.example.entity.User;
import com.example.repository.TaskRepository;
import com.example.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;


@ApplicationScoped
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
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
}

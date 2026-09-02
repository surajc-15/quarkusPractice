package com.example.service;

import com.example.dtos.TaskDto;
import com.example.dtos.TaskRequest;
import com.example.entity.Task;
import com.example.entity.User;
import com.example.entity.enums.Priority;
import com.example.entity.enums.Status;
import com.example.repository.TaskRepository;
import com.example.repository.UserRepository;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Test
    void shouldCreateTask() {

        // Arrange
        TaskRepository taskRepository = mock(TaskRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        Logger logger = mock(Logger.class);

        TaskService taskService =
                new TaskService(taskRepository, userRepository, logger);

        User user = new User();
        user.setUsername("suraj");

        when(userRepository.findByUsername("suraj"))
                .thenReturn(user);

        TaskRequest request = new TaskRequest(
                "Learn Kafka",
                "Complete Kafka tutorial",
                Status.TODO,
                Priority.HIGH
        );

        // Act
        TaskDto result =
                taskService.createTask(request, "suraj");

        // Assert
        assertNotNull(result);
        assertEquals("Learn Kafka", result.title());
        assertEquals("Complete Kafka tutorial", result.description());
        assertEquals(Status.TODO, result.status());
        assertEquals(Priority.HIGH, result.priority());

        verify(userRepository)
                .findByUsername("suraj");

        verify(taskRepository)
                .persist(any(Task.class));
    }



    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        // Arrange
        TaskRepository taskRepository = mock(TaskRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        Logger logger = mock(Logger.class);

        TaskService taskService =
                new TaskService(taskRepository, userRepository, logger);

        when(userRepository.findByUsername("suraj"))
                .thenReturn(null);

        TaskRequest request = new TaskRequest(
                "Learn Kafka",
                "Complete Kafka tutorial",
                Status.TODO,
                Priority.HIGH
        );

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskService.createTask(request, "suraj")
        );

        assertEquals("User not found", exception.getMessage());

        // Make sure task was never persisted
        verify(taskRepository, never())
                .persist(any(Task.class));
    }


    @Test
    void shouldUpdateTask() {

        // Arrange
        TaskRepository taskRepository = mock(TaskRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        Logger logger = mock(Logger.class);

        TaskService taskService =
                new TaskService(taskRepository, userRepository, logger);

        User user = new User();
        user.setUsername("suraj");

        Task task = new Task();
        task.setId(1L);
        task.setTitle("Old title");
        task.setDescription("Old description");
        task.setStatus(Status.TODO);
        task.setPriority(Priority.LOW);
        task.setUser(user);

        when(taskRepository.findByUsernameAndId("suraj", 1L))
                .thenReturn(task);

        TaskRequest request = new TaskRequest(
                "New title",
                "New description",
                Status.DONE,
                Priority.HIGH
        );

        // Act
        TaskDto result =
                taskService.updateTask(request, 1L, "suraj");

        // Assert
        assertEquals("New title", result.title());
        assertEquals("New description", result.description());
        assertEquals(Status.DONE, result.status());
        assertEquals(Priority.HIGH, result.priority());

        verify(taskRepository)
                .findByUsernameAndId("suraj", 1L);

        verify(taskRepository)
                .flush();
    }

}
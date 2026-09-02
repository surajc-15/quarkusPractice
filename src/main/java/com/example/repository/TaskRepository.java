package com.example.repository;

import com.example.entity.Task;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;


@ApplicationScoped
public class TaskRepository implements PanacheRepository<Task> {

    public List<Task> findByUser(String username) {
        return list("user.username", username);
    }

    public List<Task> findByStatus(String status) {
        return list("status", status);
    }

    public Task findByUsernameAndId(String username, Long id) {
        return find("user.username = ?1 and id = ?2", username, id).firstResult();
    }


}

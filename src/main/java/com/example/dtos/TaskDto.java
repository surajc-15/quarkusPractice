package com.example.dtos;

import com.example.entity.enums.Priority;
import com.example.entity.enums.Status;

public record TaskDto(
    Long id,
    String title,
    String description,
    Status status,
    Priority priority,
    java.time.LocalDateTime created_at,
    java.time.LocalDateTime updated_at
) {
    public String getId() {
        return id.toString();
    }
}


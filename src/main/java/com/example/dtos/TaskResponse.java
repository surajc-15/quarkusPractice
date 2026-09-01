package com.example.dtos;

import com.example.entity.enums.Priority;
import com.example.entity.enums.Status;

public record TaskResponse (Long id,
                            String title,
                            String description,
                            Status status,
                            Priority priority){
}

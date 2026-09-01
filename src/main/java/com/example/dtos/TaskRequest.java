package com.example.dtos;

import com.example.entity.enums.Priority;
import com.example.entity.enums.Status;


public record TaskRequest (
                            String title,
                            String description,
                            Status status,
                            Priority priority){
}

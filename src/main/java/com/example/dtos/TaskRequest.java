package com.example.dtos;

import com.example.entity.enums.Priority;
import com.example.entity.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record TaskRequest (
        @NotBlank
                            String title,
        @NotNull

        @NotBlank
        String description,
                            Status status,
        @NotNull

        Priority priority){
}

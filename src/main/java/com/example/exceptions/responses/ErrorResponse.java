package com.example.exceptions.responses;

public record ErrorResponse(
        int status,
        String message

) {}
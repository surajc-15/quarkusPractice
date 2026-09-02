package com.example.exceptions.responses;
public record Violation(
        String field,
        String message
) {}
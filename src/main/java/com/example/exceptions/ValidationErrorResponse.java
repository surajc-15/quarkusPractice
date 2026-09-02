package com.example.exceptions;

import com.example.exceptions.responses.Violation;

import java.util.List;

public record ValidationErrorResponse(
        String title,
        int status,
        List<Violation> violations
) {}

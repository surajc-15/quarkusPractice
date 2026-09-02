package com.example.exceptions.mappers;


import com.example.exceptions.ValidationErrorResponse;
import com.example.exceptions.responses.Violation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

@Provider
public class ConstraintViolationExceptionMapper
        implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {

        List<Violation> violations = exception
                .getConstraintViolations()
                .stream()
                .map(v -> new Violation(
                        v.getPropertyPath().toString(),
                        v.getMessage()
                ))
                .toList();

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ValidationErrorResponse(
                        "Validation failed",
                        Response.Status.BAD_REQUEST.getStatusCode(),
                        violations
                ))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
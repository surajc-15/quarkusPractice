package com.example.exceptions;

import com.example.exceptions.responses.ErrorResponse;
import com.example.exceptions.responses.Violation;
import com.example.exceptions.tasks.TaskNotFoundException;
import com.example.exceptions.tasks.UnauthorizedOperationException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.util.List;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG =
            Logger.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {

        if (exception instanceof TaskNotFoundException) {

            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(
                            404,
                            exception.getMessage()
                    ))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        if(exception instanceof UnauthorizedOperationException) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new ErrorResponse(
                            403,
                            exception.getMessage()
                    ))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        if (exception instanceof ConstraintViolationException e) {

            List<Violation> violations = e.getConstraintViolations()
                    .stream()
                    .map(v -> new Violation(
                            v.getPropertyPath().toString(),
                            v.getMessage()
                    ))
                    .toList();

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ValidationErrorResponse(
                            "Validation failed",
                            400,
                            violations
                    ))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        // Log the actual exception + stack trace
        LOG.error("Unexpected error occurred", exception);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse(
                        500,
                        exception.getMessage()
                ))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
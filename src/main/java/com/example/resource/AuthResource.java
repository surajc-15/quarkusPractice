package com.example.resource;

import com.example.dtos.*;
import com.example.exceptions.InvalidTokenException;
import com.example.service.AuthService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final AuthService authService;
    private final Logger logger = Logger.getLogger(AuthResource.class);

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @POST
    @Path("/login")
    @PermitAll
    public Response login(LoginRequest request) {

        logger.info("Login request received: " + request);

        LoginResponse response = authService.login(request);

        return Response.ok(response).build();
    }

    @POST
    @Path("/register")
    @PermitAll
    public Response register(UserDto request) {

        logger.info("Received registration request: " + request);

        AuthResponse response = authService.register(request);

        return Response.status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

    @GET
    @Path("/auto-login")
    @Authenticated
    public Response autoLogin() {

        AutoLoginResponse response = authService.autoLogin();

        return Response.ok(response).build();
    }
}
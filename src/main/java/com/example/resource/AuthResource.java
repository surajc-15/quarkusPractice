package com.example.resource;

import com.example.dtos.*;
import com.example.service.AuthService;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.logging.Logger;

import static jakarta.ws.rs.client.Entity.entity;


@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final AuthService authService;
    private final Logger logger= Logger.getLogger(AuthResource.class.getName());

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @POST
    @Path("/login")
    @PermitAll
    public Response login(LoginRequest request) {

        LoginResponse response = authService.login(request);

        return Response.ok(response).build();
    }

    @POST
    @Path("/register")
    @PermitAll
    public Response register(@Context HttpHeaders headers, UserDto request) {
        // Implement registration logic here
        logger.info("Received registration request: " + request);
        System.out.println("========== REGISTER HIT ==========");

        try{
            AuthResponse response = authService.register(request);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
    @GET
    @Path("/auto-login")
    @io.quarkus.security.Authenticated
    @Produces(MediaType.APPLICATION_JSON)
    public Response autoLogin() {
        AutoLoginResponse response = authService.autoLogin();
        return Response.ok(response).build();
    }


}
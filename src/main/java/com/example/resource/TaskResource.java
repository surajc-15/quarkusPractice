package com.example.resource;

import com.example.dtos.TaskDto;
import com.example.dtos.TaskRequest;
import com.example.dtos.TaskResponse;
import com.example.service.TaskService;
import io.quarkus.security.Authenticated;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("/tasks")
@Authenticated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TaskResource {

    private final TaskService taskService;
    private final JsonWebToken jwt;

    public TaskResource(TaskService taskService,JsonWebToken jwt) {
        this.taskService = taskService;
        this.jwt=jwt;
    }

    @GET
    @Path("/all")
    public Response getAllTasks(){
        String username= jwt.getSubject();
        List<TaskDto> tasks =taskService.getAllTasks(username);
        return Response.ok(tasks).build();
    }

    @POST
    @Path("/create")
    public Response createTask(TaskRequest taskRequest){
        TaskDto task=taskService.createTask(taskRequest, jwt.getSubject());
        return Response.ok(task).build();
    }


}

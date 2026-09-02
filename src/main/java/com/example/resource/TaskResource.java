package com.example.resource;

import com.example.dtos.TaskDto;
import com.example.dtos.TaskRequest;
import com.example.dtos.TaskResponse;
import com.example.service.TaskService;
import io.quarkus.security.Authenticated;
import jakarta.validation.Valid;
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
    public Response createTask(@Valid TaskRequest taskRequest) {

        TaskDto task = taskService.createTask(
                taskRequest,
                jwt.getSubject()
        );

        return Response.status(Response.Status.CREATED)
                .entity(task)
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response updateTask(@PathParam("id") Long id, @Valid TaskRequest taskRequest){
        TaskDto task=taskService.updateTask(taskRequest,id, jwt.getSubject());
        return Response.ok(task).build();
    }


    @GET
    @Path("/{id}")
    public Response getTaskDetails(@PathParam("id") Long id){
        TaskDto task=taskService.getTaskDetails(id, jwt.getSubject());
        return Response.ok(task).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTask(@PathParam("id") Long id) {

        taskService.deleteTask(id, jwt.getSubject());

        return Response.noContent().build();
    }

}

package com.example.resource;

import com.example.dtos.TaskDto;
import com.example.dtos.TaskRequest;
import com.example.entity.enums.Priority;
import com.example.entity.enums.Status;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class TaskResourceTest {

    private static final Logger log =
            LoggerFactory.getLogger(TaskResourceTest.class);

    private String token;

    @BeforeEach
    void login() {

        token = given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "username": "suraj2",
                            "password": "password"
                        }
                        """)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }


    @Test
    void shouldGetAllTasks() {

        given()
                .auth()
                .oauth2(token)
                .when()
                .get("/tasks/all")
                .then()
                .statusCode(200)
                .log().body();
    }


    @Test
    void shouldCreateTask() {

        TaskRequest request = new TaskRequest(
                "New title",
                "New description",
                Status.DONE,
                Priority.HIGH
        );

        TaskDto task = given()
                .auth()
                .oauth2(token)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/tasks/create")
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .log().body()
                .extract()
                .as(TaskDto.class);

        log.info("Created task with id: {} for user: suraj2", task.id());

        assertNotNull(task);
        assertNotNull(task.id());
        assertEquals("New title", task.title());
        assertEquals("New description", task.description());
        assertEquals(Status.DONE, task.status());
        assertEquals(Priority.HIGH, task.priority());
    }


    @Test
    void shouldGiveValidationError() {

        TaskRequest request = new TaskRequest(
                "",
                "New description",
                Status.DONE,
                Priority.HIGH
        );

        given()
                .auth()
                .oauth2(token)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/tasks/create")
                .then()
                .statusCode(400)
                .log().body();
    }


    @Test
    void shouldGetTaskById() {

        // 1. Create a task
        TaskRequest request = new TaskRequest(
                "Task for get test",
                "Description for get test",
                Status.TODO,
                Priority.HIGH
        );

        TaskDto createdTask = given()
                .auth()
                .oauth2(token)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/tasks/create")
                .then()
                .statusCode(201)
                .extract()
                .as(TaskDto.class);

        Long taskId = createdTask.id();

        // 2. Get the task using the ID we just created
        TaskDto task = given()
                .auth()
                .oauth2(token)
                .when()
                .get("/tasks/" + taskId)
                .then()
                .statusCode(200)
                .log().body()
                .extract()
                .as(TaskDto.class);

        // 3. Verify the returned task
        assertNotNull(task);
        assertEquals(taskId, task.id());
        assertEquals("Task for get test", task.title());
        assertEquals("Description for get test", task.description());
        assertEquals(Status.TODO, task.status());
        assertEquals(Priority.HIGH, task.priority());
    }


    @Test
    void shouldUpdateTask() {

        // 1. Create a task
        TaskRequest createRequest = new TaskRequest(
                "Original title",
                "Original description",
                Status.TODO,
                Priority.LOW
        );

        TaskDto createdTask = given()
                .auth()
                .oauth2(token)
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post("/tasks/create")
                .then()
                .statusCode(201)
                .extract()
                .as(TaskDto.class);

        Long taskId = createdTask.id();

        // 2. Update the task
        TaskRequest updateRequest = new TaskRequest(
                "Updated title",
                "Updated description",
                Status.DONE,
                Priority.HIGH
        );

        TaskDto updatedTask = given()
                .auth()
                .oauth2(token)
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put("/tasks/" + taskId)
                .then()
                .statusCode(200)
                .log().body()
                .extract()
                .as(TaskDto.class);

        // 3. Verify updated values
        assertNotNull(updatedTask);
        assertEquals(taskId, updatedTask.id());
        assertEquals("Updated title", updatedTask.title());
        assertEquals("Updated description", updatedTask.description());
        assertEquals(Status.DONE, updatedTask.status());
        assertEquals(Priority.HIGH, updatedTask.priority());
    }


    @Test
    void shouldDeleteTask() {

        // 1. Create a task
        TaskRequest request = new TaskRequest(
                "Task to delete",
                "This task should be deleted",
                Status.TODO,
                Priority.LOW
        );

        TaskDto createdTask = given()
                .auth()
                .oauth2(token)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/tasks/create")
                .then()
                .statusCode(201)
                .extract()
                .as(TaskDto.class);

        Long taskId = createdTask.id();

        // 2. Delete the task
        given()
                .auth()
                .oauth2(token)
                .when()
                .delete("/tasks/" + taskId)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        // 3. Verify the task no longer exists
        given()
                .auth()
                .oauth2(token)
                .when()
                .get("/tasks/" + taskId)
                .then()
                .statusCode(404);
    }
}
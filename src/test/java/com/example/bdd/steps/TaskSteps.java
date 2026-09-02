package com.example.bdd.steps;

import com.example.dtos.TaskRequest;
import com.example.entity.enums.Priority;
import com.example.entity.enums.Status;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskSteps {

    // Stores JWT received from login
    private String token;

    // Stores response received from create-task API
    private Response response;


    @Given("I am an authenticated user")
    public void iAmAnAuthenticatedUser() {

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


    @When("I create a task with valid details")
    public void iCreateATaskWithValidDetails() {

        TaskRequest request = new TaskRequest(
                "BDD Task",
                "Task created using BDD",
                Status.TODO,
                Priority.HIGH
        );

        response = given()
                .auth()
                .oauth2(token)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/tasks/create");
    }


    @Then("the task should be created successfully")
    public void theTaskShouldBeCreatedSuccessfully() {

        assertEquals(201, response.statusCode());
    }
}
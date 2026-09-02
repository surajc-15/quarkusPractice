package com.example.bdd.steps;

import com.example.dtos.TaskDto;
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

public class TaskAccessSteps {

    private String currentUserToken;
    private String taskOwnerToken;
    private Long taskId;
    private Response response;


    @Given("I am authenticated as user {string}")
    public void iAmAuthenticatedAsUser(String username) {
        System.out.println("Authenticating as user: " + username);
        currentUserToken = login(username,"password");

    }


    @Given("user {string} has created a task")
    public void userHasCreatedATask(String username) {
        System.out.println("Authenticating as user: " + username);
        taskOwnerToken = login(username,"password");

        TaskRequest request = new TaskRequest(
                "Private task",
                "Task belonging to another user",
                Status.TODO,
                Priority.HIGH
        );

        TaskDto task = given()
                .auth()
                .oauth2(taskOwnerToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/tasks/create")
                .then()
                .statusCode(201)
                .extract()
                .as(TaskDto.class);

        taskId = task.id();
    }


    @When("I try to access that task as user {string}")
    public void iTryToAccessThatTaskAsUser(String username) {

        // Make sure we are using the token of the user
        // who is trying to access the task.
        currentUserToken = login(username,"password");

        response = given()
                .auth()
                .oauth2(currentUserToken)
                .when()
                .get("/tasks/" + taskId);
    }


    @Then("I should receive a forbidden response")
    public void iShouldReceiveAForbiddenResponse() {

        assertEquals(403, response.statusCode());
    }


    private String login(String username,String password) {

        return given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "username": "%s",
                            "password": "%s"
                        }
                        """.formatted(username,password))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .log()
                .body()
                .extract()
                .path("token");
    }
}
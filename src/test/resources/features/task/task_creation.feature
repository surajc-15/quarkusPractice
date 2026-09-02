Feature: Task creation

  Scenario: Create a task successfully
    Given I am an authenticated user
    When I create a task with valid details
    Then the task should be created successfully
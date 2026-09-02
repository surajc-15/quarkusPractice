Feature: Task access control

  Scenario: User cannot access another user's task
    Given I am authenticated as user "suraj2"
    And user "suraj" has created a task
    When I try to access that task as user "suraj2"
    Then I should receive a forbidden response
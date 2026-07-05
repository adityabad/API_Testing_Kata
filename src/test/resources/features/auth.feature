@auth
Feature: Authentication and authorization
  As an API consumer
  I want to be authenticated and authorized
  So that only valid users can manage bookings

  Scenario: Login fails with invalid credentials
    When a user logs in with username "admin" and password "wrongpassword"
    Then the authentication should fail with status 401

  Scenario: Login fails with an empty username
    When a user logs in with username "" and password "password"
    Then the authentication should fail with status 401

  Scenario: Login fails with an empty password
    When a user logs in with username "admin" and password ""
    Then the authentication should fail with status 401

  Scenario: Login fails with both fields empty
    When a user logs in with username "" and password ""
    Then the authentication should fail with status 401

  Scenario: Login succeeds with valid credentials
    When a user logs in with username "admin" and password "password"
    Then the authentication should succeed with status 200

  Scenario: Accessing a protected endpoint without a token fails
    When a user accesses the booking endpoint without authentication
    Then the request should be rejected with status 403

  Scenario: Accessing a protected endpoint with an invalid token fails
    When a user accesses the booking endpoint with an invalid token
    Then the request should be rejected with status 403

  Scenario: Accessing a protected endpoint with a malformed token fails
    When a user accesses the booking endpoint with a malformed token
    Then the request should be rejected with status 403

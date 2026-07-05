@auth
Feature: Login and access control
  As a hotel staff member
  I want the system to verify who I am
  So that only authorized users can manage bookings

  Scenario: Staff member logs in with valid credentials
    Given a staff member has valid credentials
    When they log in
    Then they should be granted access

  Scenario: Staff member logs in with an invalid password
    Given a staff member enters the wrong password
    When they log in
    Then they should be denied access

  Scenario: Staff member leaves the username blank
    Given a staff member leaves the username blank
    When they log in
    Then they should be denied access

  Scenario: Staff member leaves the password blank
    Given a staff member leaves the password blank
    When they log in
    Then they should be denied access

  Scenario: Staff member leaves both fields blank
    Given a staff member leaves both fields blank
    When they log in
    Then they should be denied access

  Scenario: Staff member tries to manage bookings without logging in
    Given a staff member is not logged in
    When they try to view a booking
    Then they should be blocked

  Scenario: Staff member tries to manage bookings after their session has expired
    Given a staff member's session has expired
    When they try to view a booking
    Then they should be blocked

  Scenario: Staff member tries to manage bookings with corrupted session details
    Given a staff member's session details are corrupted
    When they try to view a booking
    Then they should be blocked

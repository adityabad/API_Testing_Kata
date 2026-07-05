@booking @delete
Feature: Delete a room booking
  As a hotel administrator
  I want to delete a booking
  So that cancelled reservations are removed from the system

  Background:
    Given the hotel booking service is available

  Scenario: Successfully delete an existing booking
    Given a booking has been created with the following details:
      | firstname   | John                 |
      | lastname    | Smith                |
      | email       | john.smith@email.com |
      | phone       | 15551234567          |
      | checkin     | 2026-08-16           |
      | checkout    | 2026-08-17           |
      | depositpaid | true                 |
    When a guest deletes the booking
    Then the booking should be deleted successfully
    And retrieving the deleted booking should return not found

  Scenario: Fail to delete a booking that does not exist
    When a guest deletes a booking with id 999999
    Then the booking should not be found

  Scenario: Fail to delete a booking with id zero
    When a guest deletes a booking with id 0
    Then the booking should not be found

  Scenario: Fail to delete an already deleted booking
    Given a booking has been created with the following details:
      | firstname   | Jane                 |
      | lastname    | Doe                  |
      | email       | jane.doe@email.com   |
      | phone       | 15559876543          |
      | checkin     | 2026-09-10           |
      | checkout    | 2026-09-12           |
      | depositpaid | false                |
    When a guest deletes the booking
    Then the booking should be deleted successfully
    When a guest deletes the booking
    Then the booking should not be found

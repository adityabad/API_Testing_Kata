@booking @delete
Feature: Delete a room booking
  As a hotel administrator
  I want to delete a booking
  So that cancelled reservations are removed from the system

  Background:
    Given the hotel booking service is available

  Scenario: Successfully delete an existing booking
    Given a booking has been created with the following details:
      | firstname   | dddd          |
      | lastname    | lll           |
      | email       | asdfafk@dv.co |
      | phone       | 99888888888   |
      | checkin     | 2026-08-16    |
      | checkout    | 2026-08-17    |
      | depositpaid | true          |
    When a guest deletes the booking
    Then the booking should be deleted successfully
    And retrieving the deleted booking should return not found

  Scenario: Fail to delete a booking that does not exist
    When a guest deletes a booking with id 999999
    Then the booking should not be found

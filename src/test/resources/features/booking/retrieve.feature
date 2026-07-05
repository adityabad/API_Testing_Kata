@booking @retrieve
Feature: Retrieve a room booking
  As a hotel administrator
  I want to retrieve a booking by its id
  So that I can review the reservation details

  Background:
    Given the hotel booking service is available

  Scenario: Successfully retrieve an existing booking
    Given a booking has been created with the following details:
      | firstname   | John               |
      | lastname    | Smith              |
      | email       | john.smith@email.com |
      | phone       | 15551234567        |
      | checkin     | 2026-08-16         |
      | checkout    | 2026-08-17         |
      | depositpaid | true               |
    When a guest retrieves the booking
    Then the booking details should be returned successfully

  Scenario: Fail to retrieve a booking that does not exist
    When a guest retrieves a booking with id 999999
    Then the booking should not be found

  Scenario: Fail to retrieve a booking with id zero
    When a guest retrieves a booking with id 0
    Then the booking should not be found

  Scenario: Fail to retrieve a booking with a negative id
    When a guest retrieves a booking with id -1
    Then the booking should not be found

  Scenario: Fail to retrieve a deleted booking
    Given a booking has been created with the following details:
      | firstname   | Jane               |
      | lastname    | Doe                |
      | email       | jane.doe@email.com |
      | phone       | 15559876543        |
      | checkin     | 2026-09-10         |
      | checkout    | 2026-09-12         |
      | depositpaid | false              |
    When a guest deletes the booking
    Then the booking should be deleted successfully
    When a guest retrieves the booking
    Then the booking should not be found

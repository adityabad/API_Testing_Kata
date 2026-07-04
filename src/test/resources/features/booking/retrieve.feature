@booking @retrieve
Feature: Retrieve a room booking
  As a hotel administrator
  I want to retrieve a booking by its id
  So that I can review the reservation details

  Background:
    Given the hotel booking service is available

  Scenario: Successfully retrieve an existing booking
    Given a booking has been created with the following details:
      | firstname   | dddd          |
      | lastname    | lll           |
      | email       | asdfafk@dv.co |
      | phone       | 99888888888   |
      | checkin     | 2026-08-16    |
      | checkout    | 2026-08-17    |
      | depositpaid | true          |
    When a guest retrieves the booking
    Then the booking details should be returned successfully

  Scenario: Fail to retrieve a booking that does not exist
    When a guest retrieves a booking with id 999999
    Then the booking should not be found

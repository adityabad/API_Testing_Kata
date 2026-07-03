@booking
Feature: Create a new room booking
  As a prospective hotel guest
  I want to submit a room booking request
  So that my reservation is recorded in the system

  Background:
    Given the hotel booking service is available

  Scenario: Successfully create a booking with valid guest details
    When a guest submits a booking with the following details:
      | firstname   | dddd         |
      | lastname    | lll          |
      | email       | asdfafk@dv.co|
      | phone       | 99888888888  |
      | checkin     | 2026-08-16   |
      | checkout    | 2026-08-17   |
      | depositpaid | true         |
    Then the booking should be created successfully
    And the response should include a booking confirmation

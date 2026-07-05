@booking @create
Feature: Create a new room booking
  As a prospective hotel guest
  I want to submit a room booking request
  So that my reservation is recorded in the system

  Background:
    Given the hotel booking service is available

  Scenario: Successfully create a booking with valid guest details
    When a guest submits a booking with the following details:
      | firstname   | dddd          |
      | lastname    | lll           |
      | email       | asdfafk@dv.co |
      | phone       | 99888888888   |
      | checkin     | 2026-08-16    |
      | checkout    | 2026-08-17    |
      | depositpaid | true          |
    Then the booking should be created successfully
    And the response should include a booking confirmation

  Scenario Outline: Successfully create a booking with boundary value inputs
    When a guest submits a booking with the following details:
      | firstname   | <firstname>   |
      | lastname    | <lastname>    |
      | email       | <email>       |
      | phone       | <phone>       |
      | checkin     | <checkin>     |
      | checkout    | <checkout>    |
      | depositpaid | <depositpaid> |
    Then the booking should be created successfully
    And the response should include a booking confirmation

    Examples:
      | firstname            | lastname             | email                | phone               | checkin    | checkout   | depositpaid |
      | abc                  | xyz                  | test@example.com     | 99888888888         | 2026-08-16 | 2026-08-17 | true        |
      | abcdefghijklmnopqr   | xyz                  | test@example.com     | 99888888888         | 2026-08-16 | 2026-08-17 | true        |
      | abc                  | abcdefghijklmnopqr   | test@example.com     | 99888888888         | 2026-08-16 | 2026-08-17 | true        |
      | abc                  | xyz                  | test@example.com     | 99888888888         | 2026-08-16 | 2026-08-17 | false       |
      | abc                  | xyz                  | test@example.com     | 123456789012345678901 | 2026-08-16 | 2026-08-17 | true      |

  Scenario Outline: Fail to create a booking with invalid guest details
    When a guest submits a booking with the following details:
      | firstname   | <firstname>   |
      | lastname    | <lastname>    |
      | email       | <email>       |
      | phone       | <phone>       |
      | checkin     | <checkin>     |
      | checkout    | <checkout>    |
      | depositpaid | <depositpaid> |
    Then the booking should not be created
    And the response should contain the validation error "<error>"

    Examples:
      | firstname | lastname | email             | phone         | checkin    | checkout   | depositpaid | error                                |
      | dd        | lll      | asdfafk@dv.co     | 99888888888   | 2026-08-16 | 2026-08-17 | true        | size must be between 3 and 18        |
      | dddd      | l        | asdfafk@dv.co     | 99888888888   | 2026-08-16 | 2026-08-17 | true        | size must be between 3 and 18        |
      | dddd      | lll      | invalid-email     | 99888888888   | 2026-08-16 | 2026-08-17 | true        | must be a well-formed email address  |
      | dddd      | lll      | asdfafk@dv.co     | 9988888888    | 2026-08-16 | 2026-08-17 | true        | size must be between 11 and 21       |
      | dddd      | lll      | plainaddress      | 99888888888   | 2026-08-16 | 2026-08-17 | true        | must be a well-formed email address  |
      | dddd      | lll      | missing@domain    | 99888888888   | 2026-08-16 | 2026-08-17 | true        | must be a well-formed email address  |
      | dddd      | lll      | asdfafk@dv.co     | 99888888888   | 2026-08-18 | 2026-08-17 | true        | Failed to create booking             |

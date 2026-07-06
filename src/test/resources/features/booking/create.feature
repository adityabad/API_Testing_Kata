@booking @create
Feature: Create a new room booking
  As a prospective hotel guest
  I want to submit a room booking request
  So that my reservation is recorded in the system

  Background:
    Given the hotel booking service is available

  Scenario: Successfully create a booking with valid guest details
    When a guest submits a booking with the following details:
      | firstname   | John               |
      | lastname    | Smith              |
      | email       | john.smith@email.com |
      | phone       | 15551234567        |
      | checkin     | 2026-08-16         |
      | checkout    | 2026-08-17         |
      | depositpaid | true               |
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
      | firstname            | lastname             | email                       | phone                  | checkin    | checkout   | depositpaid |
      | Ann                  | Lee                  | ann.lee@email.com           | 15551234567            | 2026-08-16 | 2026-08-17 | true        |
      | AlexanderChristoph   | Smith                | alex.smith@email.com        | 15551234567            | 2026-08-16 | 2026-08-17 | true        |
      | John                 | AlexanderChristoph   | john.christoph@email.com    | 15551234567            | 2026-08-16 | 2026-08-17 | true        |
      | John                 | Smith                | john.smith@email.com        | 15551234567            | 2026-08-16 | 2026-08-17 | false       |
      | Jane                 | Smith                | jane.smith@email.com        | +1 555 123 4567 89012  | 2026-08-16 | 2026-08-17 | true        |

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
      | firstname | lastname | email                  | phone         | checkin    | checkout   | depositpaid | error                                |
      | Jo        | Smith    | john.smith@email.com   | 15551234567   | 2026-08-16 | 2026-08-17 | true        | size must be between 3 and 18        |
      | John      | S        | john.smith@email.com   | 15551234567   | 2026-08-16 | 2026-08-17 | true        | size must be between 3 and 30        |
      | John      | Smith    | invalid-email          | 15551234567   | 2026-08-16 | 2026-08-17 | true        | must be a well-formed email address  |
      | John      | Smith    | john.smith@email.com   | 1555123456    | 2026-08-16 | 2026-08-17 | true        | size must be between 11 and 21       |
      | John      | Smith    | plainaddress           | 15551234567   | 2026-08-16 | 2026-08-17 | true        | must be a well-formed email address  |
      | John      | Smith    | missing@domain         | 15551234567   | 2026-08-16 | 2026-08-17 | true        | must be a well-formed email address  |
      | John      | Smith    | john.smith@email.com   | 15551234567   | 2026-08-18 | 2026-08-17 | true        | Failed to create booking             |

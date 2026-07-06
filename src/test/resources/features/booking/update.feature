@booking @update
Feature: Update an existing room booking
  As a hotel administrator
  I want to update a booking
  So that guest details remain accurate

  Background:
    Given the hotel booking service is available

  Scenario: Successfully replace an existing booking
    Given a booking has been created with the following details:
      | firstname   | John                 |
      | lastname    | Smith                |
      | email       | john.smith@email.com |
      | phone       | 15551234567          |
      | checkin     | 2026-08-16           |
      | checkout    | 2026-08-17           |
      | depositpaid | true                 |
    When a guest updates the booking with the following details:
      | firstname   | Robert              |
      | lastname    | Johnson             |
      | email       | robert.j@email.com  |
      | phone       | 15559871234         |
      | checkin     | 2026-09-01          |
      | checkout    | 2026-09-05          |
      | depositpaid | false               |
    Then the booking should be updated successfully

  Scenario: Partial update is not supported by the API
    Given a booking has been created with the following details:
      | firstname   | Emily                 |
      | lastname    | Davis                 |
      | email       | emily.davis@email.com |
      | phone       | 15551112222           |
      | checkin     | 2026-08-16            |
      | checkout    | 2026-08-17            |
      | depositpaid | true                  |
    When a guest partially updates the booking with:
      | firstname   | Emma  |
      | depositpaid | false |
    Then the partial update should be rejected as method not allowed

  Scenario: Fail to update a booking that does not exist
    When a guest updates the booking with id 999999 with the following details:
      | firstname   | Robert              |
      | lastname    | Johnson             |
      | email       | robert.j@email.com  |
      | phone       | 15559871234         |
      | checkin     | 2026-09-01          |
      | checkout    | 2026-09-05          |
      | depositpaid | false               |
    Then the booking should not be found

  Scenario: Fail to update a booking with an invalid email
    Given a booking has been created with the following details:
      | firstname   | Michael                 |
      | lastname    | Brown                   |
      | email       | michael.brown@email.com |
      | phone       | 15552223333             |
      | checkin     | 2026-08-16              |
      | checkout    | 2026-08-17              |
      | depositpaid | true                    |
    When a guest updates the booking with the following details:
      | firstname   | Robert          |
      | lastname    | Johnson         |
      | email       | invalid-email   |
      | phone       | 15559871234     |
      | checkin     | 2026-09-01      |
      | checkout    | 2026-09-05      |
      | depositpaid | false           |
    Then the booking should not be created

  Scenario: Fail to update a booking with a phone number that is too short
    Given a booking has been created with the following details:
      | firstname   | Sarah                 |
      | lastname    | Wilson                |
      | email       | sarah.wilson@email.com |
      | phone       | 15553334444           |
      | checkin     | 2026-08-16            |
      | checkout    | 2026-08-17            |
      | depositpaid | true                  |
    When a guest updates the booking with the following details:
      | firstname   | Robert          |
      | lastname    | Johnson         |
      | email       | robert.j@email.com |
      | phone       | 1555987123      |
      | checkin     | 2026-09-01      |
      | checkout    | 2026-09-05      |
      | depositpaid | false           |
    Then the booking should not be created

  Scenario: Fail to update a booking with invalid dates
    Given a booking has been created with the following details:
      | firstname   | David                 |
      | lastname    | Miller                |
      | email       | david.miller@email.com |
      | phone       | 15554445555           |
      | checkin     | 2026-08-16            |
      | checkout    | 2026-08-17            |
      | depositpaid | true                  |
    When a guest updates the booking with the following details:
      | firstname   | Robert             |
      | lastname    | Johnson            |
      | email       | robert.j@email.com |
      | phone       | 15559871234        |
      | checkin     | 2026-09-05         |
      | checkout    | 2026-09-01         |
      | depositpaid | false              |
    Then the booking should not be created

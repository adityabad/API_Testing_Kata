@booking @update
Feature: Update an existing room booking
  As a hotel administrator
  I want to update a booking
  So that guest details remain accurate

  Background:
    Given the hotel booking service is available

  Scenario: Successfully replace an existing booking
    Given a booking has been created with the following details:
      | firstname   | dddd          |
      | lastname    | lll           |
      | email       | asdfafk@dv.co |
      | phone       | 99888888888   |
      | checkin     | 2026-08-16    |
      | checkout    | 2026-08-17    |
      | depositpaid | true          |
    When a guest updates the booking with the following details:
      | firstname   | John            |
      | lastname    | Doe             |
      | email       | john.doe@dv.co  |
      | phone       | 99999999999     |
      | checkin     | 2026-09-01      |
      | checkout    | 2026-09-05      |
      | depositpaid | false           |
    Then the booking should be updated successfully

  Scenario: Successfully partially update an existing booking
    Given a booking has been created with the following details:
      | firstname   | dddd          |
      | lastname    | lll           |
      | email       | asdfafk@dv.co |
      | phone       | 99888888888   |
      | checkin     | 2026-08-16    |
      | checkout    | 2026-08-17    |
      | depositpaid | true          |
    When a guest partially updates the booking with:
      | firstname   | Jane  |
      | depositpaid | false |
    Then the booking should be partially updated successfully
    And the booking should reflect the partial update "firstname" with value "Jane"
    And the booking should reflect the partial update "depositpaid" with value "false"

  Scenario: Fail to update a booking that does not exist
    When a guest updates the booking with id 999999 with the following details:
      | firstname   | John            |
      | lastname    | Doe             |
      | email       | john.doe@dv.co  |
      | phone       | 99999999999     |
      | checkin     | 2026-09-01      |
      | checkout    | 2026-09-05      |
      | depositpaid | false           |
    Then the booking should not be found

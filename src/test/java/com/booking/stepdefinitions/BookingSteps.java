package com.booking.stepdefinitions;

import com.booking.clients.BookingClient;
import com.booking.clients.HealthCheckClient;
import com.booking.models.BookingDates;
import com.booking.models.BookingRequest;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.Map;

import static org.hamcrest.Matchers.*;

public class BookingSteps {

    private final BookingClient bookingClient = new BookingClient();
    private final HealthCheckClient healthCheckClient = new HealthCheckClient();
    private Response response;

    @Given("the hotel booking service is available")
    public void theHotelBookingServiceIsAvailable() {
        Response healthResponse = healthCheckClient.checkHealth();
        healthResponse.then()
                .assertThat()
                .statusCode(200)
                .body("status", equalTo("UP"));
    }

    @When("a guest submits a booking with the following details:")
    public void aGuestSubmitsABookingWithTheFollowingDetails(DataTable dataTable) {
        Map<String, String> bookingData = dataTable.asMap();

        BookingDates bookingDates = BookingDates.builder()
                .checkin(bookingData.get("checkin"))
                .checkout(bookingData.get("checkout"))
                .build();

        BookingRequest bookingRequest = BookingRequest.builder()
                .firstname(bookingData.get("firstname"))
                .lastname(bookingData.get("lastname"))
                .email(bookingData.get("email"))
                .phone(bookingData.get("phone"))
                .depositpaid(Boolean.parseBoolean(bookingData.get("depositpaid")))
                .roomid((int) (Math.random() * 1000))
                .bookingdates(bookingDates)
                .build();

        response = bookingClient.createBooking(bookingRequest);
    }

    @Then("the booking should be created successfully")
    public void theBookingShouldBeCreatedSuccessfully() {
        response.then().assertThat().statusCode(201);
    }

    @And("the response should include a booking confirmation")
    public void theResponseShouldIncludeABookingConfirmation() {
        response.then()
                .body("bookingid", notNullValue())
                .body("firstname", notNullValue())
                .body("lastname", notNullValue())
                .body("roomid", notNullValue());
    }

    @Then("the booking should not be created")
    public void theBookingShouldNotBeCreated() {
        response.then().assertThat().statusCode(400);
    }

    @And("the response should contain the validation error {string}")
    public void theResponseShouldContainTheValidationError(String expectedError) {
        response.then()
                .body("errors", hasItem(expectedError));
    }
}

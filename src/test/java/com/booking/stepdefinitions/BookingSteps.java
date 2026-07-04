package com.booking.stepdefinitions;

import com.booking.clients.BookingClient;
import com.booking.clients.HealthCheckClient;
import com.booking.context.BookingContext;
import com.booking.models.BookingDates;
import com.booking.models.BookingRequest;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

public class BookingSteps {

    private final BookingClient bookingClient;
    private final HealthCheckClient healthCheckClient;
    private final BookingContext context;

    public BookingSteps(BookingContext context) {
        this.context = context;
        this.bookingClient = new BookingClient();
        this.healthCheckClient = new HealthCheckClient();
    }

    @After
    public void tearDown() {
        context.reset();
    }

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
        BookingRequest bookingRequest = buildBookingRequest(dataTable.asMap());
        Response response = bookingClient.createBooking(bookingRequest);
        context.setResponse(response);

        if (response.getStatusCode() == 200) {
            context.setBookingCreated(true);
            context.setBookingId(response.jsonPath().getInt("bookingid"));
        }
    }

    @When("a guest retrieves the booking")
    public void aGuestRetrievesTheBooking() {
        Response response = bookingClient.getBooking(context.getBookingId());
        context.setResponse(response);
    }

    @When("a guest retrieves a booking with id {int}")
    public void aGuestRetrievesABookingWithId(int bookingId) {
        Response response = bookingClient.getBooking(bookingId);
        context.setResponse(response);
    }

    @When("a guest updates the booking with the following details:")
    public void aGuestUpdatesTheBookingWithTheFollowingDetails(DataTable dataTable) {
        BookingRequest bookingRequest = buildBookingRequest(dataTable.asMap());
        Response response = bookingClient.updateBooking(context.getBookingId(), bookingRequest);
        context.setResponse(response);
    }

    @When("a guest updates the booking with id {int} with the following details:")
    public void aGuestUpdatesTheBookingWithIdWithTheFollowingDetails(int bookingId, DataTable dataTable) {
        BookingRequest bookingRequest = buildBookingRequest(dataTable.asMap());
        Response response = bookingClient.updateBooking(bookingId, bookingRequest);
        context.setResponse(response);
    }

    @When("a guest partially updates the booking with:")
    public void aGuestPartiallyUpdatesTheBookingWith(DataTable dataTable) {
        Map<String, String> rawUpdates = dataTable.asMap();
        Map<String, Object> updates = new HashMap<>();

        for (Map.Entry<String, String> entry : rawUpdates.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if ("depositpaid".equalsIgnoreCase(key)) {
                updates.put(key, Boolean.parseBoolean(value));
            } else {
                updates.put(key, value);
            }
        }

        Response response = bookingClient.patchBooking(context.getBookingId(), updates);
        context.setResponse(response);
    }

    @When("a guest deletes the booking")
    public void aGuestDeletesTheBooking() {
        Response response = bookingClient.deleteBooking(context.getBookingId());
        context.setResponse(response);
    }

    @When("a guest deletes a booking with id {int}")
    public void aGuestDeletesABookingWithId(int bookingId) {
        Response response = bookingClient.deleteBooking(bookingId);
        context.setResponse(response);
    }

    @Then("the booking should be created successfully")
    public void theBookingShouldBeCreatedSuccessfully() {
        context.getResponse().then().assertThat().statusCode(200);
    }

    @Then("the booking should not be created")
    public void theBookingShouldNotBeCreated() {
        context.getResponse().then().assertThat().statusCode(400);
    }

    @And("the response should include a booking confirmation")
    public void theResponseShouldIncludeABookingConfirmation() {
        context.getResponse().then()
                .body("bookingid", notNullValue())
                .body("booking.firstname", notNullValue())
                .body("booking.lastname", notNullValue())
                .body("booking.roomid", notNullValue());
    }

    @And("the response should contain the validation error {string}")
    public void theResponseShouldContainTheValidationError(String expectedError) {
        context.getResponse().then()
                .body("errors", hasItem(expectedError));
    }

    @Then("the booking details should be returned successfully")
    public void theBookingDetailsShouldBeReturnedSuccessfully() {
        context.getResponse().then()
                .assertThat()
                .statusCode(200)
                .body("firstname", notNullValue())
                .body("lastname", notNullValue())
                .body("roomid", notNullValue())
                .body("bookingdates", notNullValue());
    }

    @Then("the booking should not be found")
    public void theBookingShouldNotBeFound() {
        context.getResponse().then().assertThat().statusCode(404);
    }

    @Then("the booking should be updated successfully")
    public void theBookingShouldBeUpdatedSuccessfully() {
        context.getResponse().then()
                .assertThat()
                .statusCode(200)
                .body("firstname", notNullValue())
                .body("lastname", notNullValue());
    }

    @Then("the booking should be partially updated successfully")
    public void theBookingShouldBePartiallyUpdatedSuccessfully() {
        context.getResponse().then()
                .assertThat()
                .statusCode(200)
                .body("firstname", notNullValue())
                .body("lastname", notNullValue());
    }

    @And("the booking should reflect the partial update {string} with value {string}")
    public void theBookingShouldReflectThePartialUpdateWithValue(String field, String value) {
        if ("depositpaid".equalsIgnoreCase(field)) {
            context.getResponse().then().body(field, equalTo(Boolean.parseBoolean(value)));
        } else {
            context.getResponse().then().body(field, equalTo(value));
        }
    }

    @Then("the booking should be deleted successfully")
    public void theBookingShouldBeDeletedSuccessfully() {
        context.getResponse().then().assertThat().statusCode(201);
    }

    @And("retrieving the deleted booking should return not found")
    public void retrievingTheDeletedBookingShouldReturnNotFound() {
        Response response = bookingClient.getBooking(context.getBookingId());
        response.then().assertThat().statusCode(404);
    }

    @Given("a booking has been created with the following details:")
    public void aBookingHasBeenCreatedWithTheFollowingDetails(DataTable dataTable) {
        aGuestSubmitsABookingWithTheFollowingDetails(dataTable);
        theBookingShouldBeCreatedSuccessfully();
        theResponseShouldIncludeABookingConfirmation();
    }

    private BookingRequest buildBookingRequest(Map<String, String> bookingData) {
        BookingDates bookingDates = BookingDates.builder()
                .checkin(bookingData.get("checkin"))
                .checkout(bookingData.get("checkout"))
                .build();

        return BookingRequest.builder()
                .firstname(bookingData.get("firstname"))
                .lastname(bookingData.get("lastname"))
                .email(bookingData.get("email"))
                .phone(bookingData.get("phone"))
                .depositpaid(Boolean.parseBoolean(bookingData.get("depositpaid")))
                .roomid((int) (Math.random() * 1000))
                .bookingdates(bookingDates)
                .build();
    }
}

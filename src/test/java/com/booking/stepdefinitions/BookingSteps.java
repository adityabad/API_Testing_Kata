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

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class BookingSteps {

    private final BookingClient bookingClient = new BookingClient();
    private final HealthCheckClient healthCheckClient = new HealthCheckClient();
    private final BookingContext context = new BookingContext();

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
                .body("status", equalTo("UP"))
                .contentType(containsString("json"))
                .body(matchesJsonSchemaInClasspath("schemas/health-schema.json"));
    }

    @When("a guest submits a booking with the following details:")
    public void aGuestSubmitsABookingWithTheFollowingDetails(DataTable dataTable) {
        BookingRequest bookingRequest = buildBookingRequest(dataTable.asMap());
        context.setLastBookingRequest(bookingRequest);

        Response response = bookingClient.createBooking(bookingRequest);
        context.setResponse(response);

        if (response.getStatusCode() == 201) {
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
        context.setLastBookingRequest(bookingRequest);

        Response response = bookingClient.updateBooking(context.getBookingId(), bookingRequest);
        context.setResponse(response);
    }

    @When("a guest updates the booking with id {int} with the following details:")
    public void aGuestUpdatesTheBookingWithIdWithTheFollowingDetails(int bookingId, DataTable dataTable) {
        BookingRequest bookingRequest = buildBookingRequest(dataTable.asMap());
        context.setLastBookingRequest(bookingRequest);

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

        context.setLastPartialUpdate(updates);

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
        context.getResponse().then()
                .assertThat()
                .statusCode(201)
                .contentType("application/json")
                .body(matchesJsonSchemaInClasspath("schemas/booking-create-response-schema.json"));
    }

    @Then("the booking should not be created")
    public void theBookingShouldNotBeCreated() {
        context.getResponse().then()
                .assertThat()
                .statusCode(400)
                .contentType("application/json")
                .body("errors", notNullValue())
                .body(matchesJsonSchemaInClasspath("schemas/validation-error-schema.json"));
    }

    @And("the response should include a booking confirmation")
    public void theResponseShouldIncludeABookingConfirmation() {
        BookingRequest request = context.getLastBookingRequest();

        context.getResponse().then()
                .body("bookingid", greaterThan(0))
                .body("firstname", equalTo(request.getFirstname()))
                .body("lastname", equalTo(request.getLastname()))
                .body("depositpaid", equalTo(request.isDepositpaid()))
                .body("roomid", equalTo(request.getRoomid()))
                .body("bookingdates.checkin", equalTo(request.getBookingdates().getCheckin()))
                .body("bookingdates.checkout", equalTo(request.getBookingdates().getCheckout()));
    }

    @And("the response should contain the validation error {string}")
    public void theResponseShouldContainTheValidationError(String expectedError) {
        context.getResponse().then()
                .body("errors", hasItem(expectedError));
    }

    @Then("the booking details should be returned successfully")
    public void theBookingDetailsShouldBeReturnedSuccessfully() {
        BookingRequest request = context.getLastBookingRequest();

        context.getResponse().then()
                .assertThat()
                .statusCode(200)
                .contentType("application/json")
                .body(matchesJsonSchemaInClasspath("schemas/booking-schema.json"))
                .body("firstname", equalTo(request.getFirstname()))
                .body("lastname", equalTo(request.getLastname()))
                .body("depositpaid", equalTo(request.isDepositpaid()))
                .body("roomid", equalTo(request.getRoomid()))
                .body("bookingdates.checkin", equalTo(request.getBookingdates().getCheckin()))
                .body("bookingdates.checkout", equalTo(request.getBookingdates().getCheckout()));
    }

    @Then("the booking should not be found")
    public void theBookingShouldNotBeFound() {
        context.getResponse().then()
                .assertThat()
                .statusCode(404);
        SchemaValidator.validateIfBodyPresent(context.getResponse(), "schemas/not-found-error-schema.json");
    }

    @Then("the booking should be updated successfully")
    public void theBookingShouldBeUpdatedSuccessfully() {
        BookingRequest request = context.getLastBookingRequest();

        context.getResponse().then()
                .assertThat()
                .statusCode(200)
                .contentType("application/json")
                .body(matchesJsonSchemaInClasspath("schemas/booking-update-response-schema.json"))
                .body("booking.firstname", equalTo(request.getFirstname()))
                .body("booking.lastname", equalTo(request.getLastname()))
                .body("booking.depositpaid", equalTo(request.isDepositpaid()))
                .body("booking.bookingdates.checkin", equalTo(request.getBookingdates().getCheckin()))
                .body("booking.bookingdates.checkout", equalTo(request.getBookingdates().getCheckout()));
    }

    @Then("the partial update should be rejected as method not allowed")
    public void thePartialUpdateShouldBeRejectedAsMethodNotAllowed() {
        context.getResponse().then()
                .assertThat()
                .statusCode(405);
    }

    @And("the booking should reflect the partial update {string} with value {string}")
    public void theBookingShouldReflectThePartialUpdateWithValue(String field, String value) {
        Object expectedValue = context.getLastPartialUpdate().get(field);
        context.getResponse().then().body(field, equalTo(expectedValue));
    }

    @Then("the booking should be deleted successfully")
    public void theBookingShouldBeDeletedSuccessfully() {
        context.getResponse().then()
                .assertThat()
                .statusCode(202);
    }

    @And("retrieving the deleted booking should return not found")
    public void retrievingTheDeletedBookingShouldReturnNotFound() {
        Response response = bookingClient.getBooking(context.getBookingId());
        response.then()
                .assertThat()
                .statusCode(404);
        SchemaValidator.validateIfBodyPresent(response, "schemas/not-found-error-schema.json");
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

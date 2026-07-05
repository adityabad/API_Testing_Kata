package com.booking.stepdefinitions;

import com.booking.clients.AuthClient;
import com.booking.clients.BookingClient;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class AuthSteps {

    private final BookingClient bookingClient = new BookingClient();
    private Response response;

    @When("a user logs in with username {string} and password {string}")
    public void aUserLogsInWithUsernameAndPassword(String username, String password) {
        response = AuthClient.login(username, password);
    }

    @When("a user accesses the booking endpoint without authentication")
    public void aUserAccessesTheBookingEndpointWithoutAuthentication() {
        // Using a high id to avoid accidentally hitting an existing booking.
        response = bookingClient.getBookingWithoutAuth(999999);
    }

    @When("a user accesses the booking endpoint with an invalid token")
    public void aUserAccessesTheBookingEndpointWithAnInvalidToken() {
        // Using a high id to avoid accidentally hitting an existing booking.
        response = bookingClient.getBookingWithInvalidToken(999999);
    }

    @When("a user accesses the booking endpoint with a malformed token")
    public void aUserAccessesTheBookingEndpointWithAMalformedToken() {
        // Using a high id to avoid accidentally hitting an existing booking.
        response = bookingClient.getBookingWithMalformedToken(999999);
    }

    @Then("the authentication should fail with status {int}")
    public void theAuthenticationShouldFailWithStatus(int expectedStatus) {
        response.then()
                .assertThat()
                .statusCode(expectedStatus)
                .contentType("application/json")
                .body(matchesJsonSchemaInClasspath("schemas/auth-error-schema.json"))
                .body("error", equalTo("Invalid credentials"));
    }

    @Then("the authentication should succeed with status {int}")
    public void theAuthenticationShouldSucceedWithStatus(int expectedStatus) {
        response.then()
                .assertThat()
                .statusCode(expectedStatus)
                .contentType("application/json")
                .body("token", notNullValue());
    }

    @Then("the request should be rejected with status {int}")
    public void theRequestShouldBeRejectedWithStatus(int expectedStatus) {
        response.then()
                .assertThat()
                .statusCode(expectedStatus);
        SchemaValidator.validateIfBodyPresent(response, "schemas/unauthorized-error-schema.json");
    }
}

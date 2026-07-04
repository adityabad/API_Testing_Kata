package com.booking.stepdefinitions;

import com.booking.clients.AuthClient;
import com.booking.clients.BookingClient;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

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

    @Then("the authentication should fail with status {int}")
    public void theAuthenticationShouldFailWithStatus(int expectedStatus) {
        response.then()
                .assertThat()
                .statusCode(expectedStatus)
                .contentType("application/json")
                .body("error", equalTo("Invalid credentials"));
    }

    @Then("the request should be rejected with status {int}")
    public void theRequestShouldBeRejectedWithStatus(int expectedStatus) {
        response.then()
                .assertThat()
                .statusCode(expectedStatus);
    }
}

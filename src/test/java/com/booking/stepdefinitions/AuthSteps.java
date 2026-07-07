package com.booking.stepdefinitions;

import com.booking.clients.AuthClient;
import com.booking.clients.BookingClient;
import com.booking.utils.ConfigReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class AuthSteps {

    private final BookingClient bookingClient = new BookingClient();
    private Response response;
    private String username;
    private String password;
    private AuthState authState = AuthState.NONE;

    private enum AuthState {
        NONE,
        NOT_LOGGED_IN,
        EXPIRED_TOKEN,
        CORRUPTED_TOKEN
    }

    @Given("a staff member has valid credentials")
    public void aStaffMemberHasValidCredentials() {
        this.username = ConfigReader.getProperty("username");
        this.password = ConfigReader.getProperty("password");
    }

    @Given("a staff member enters the wrong password")
    public void aStaffMemberEntersTheWrongPassword() {
        this.username = ConfigReader.getProperty("username");
        this.password = "wrongpassword";
    }

    @Given("a staff member leaves the username blank")
    public void aStaffMemberLeavesTheUsernameBlank() {
        this.username = "";
        this.password = ConfigReader.getProperty("password");
    }

    @Given("a staff member leaves the password blank")
    public void aStaffMemberLeavesThePasswordBlank() {
        this.username = ConfigReader.getProperty("username");
        this.password = "";
    }

    @Given("a staff member leaves both fields blank")
    public void aStaffMemberLeavesBothFieldsBlank() {
        this.username = "";
        this.password = "";
    }

    @Given("a staff member is not logged in")
    public void aStaffMemberIsNotLoggedIn() {
        this.authState = AuthState.NOT_LOGGED_IN;
    }

    @Given("a staff member's session has expired")
    public void aStaffMemberSessionHasExpired() {
        this.authState = AuthState.EXPIRED_TOKEN;
    }

    @Given("a staff member's session details are corrupted")
    public void aStaffMemberSessionDetailsAreCorrupted() {
        this.authState = AuthState.CORRUPTED_TOKEN;
    }

    @When("they log in")
    public void theyLogIn() {
        response = AuthClient.login(username, password);
    }

    @When("they try to view a booking")
    public void theyTryToViewABooking() {
        switch (authState) {
            case NOT_LOGGED_IN:
                response = bookingClient.getBookingWithoutAuth(999999);
                break;
            case EXPIRED_TOKEN:
                response = bookingClient.getBookingWithInvalidToken(999999);
                break;
            case CORRUPTED_TOKEN:
                response = bookingClient.getBookingWithMalformedToken(999999);
                break;
            default:
                throw new IllegalStateException("No authorization state was set before attempting to view a booking");
        }
    }

    @Then("they should be granted access")
    public void theyShouldBeGrantedAccess() {
        response.then()
                .assertThat()
                .statusCode(200)
                .contentType("application/json")
                .body("token", notNullValue());
    }

    @Then("they should be denied access")
    public void theyShouldBeDeniedAccess() {
        response.then()
                .assertThat()
                .statusCode(401)
                .contentType("application/json")
                .body(matchesJsonSchemaInClasspath("schemas/auth-error-schema.json"))
                .body("error", equalTo("Invalid credentials"));
    }

    @Then("they should be blocked")
    public void theyShouldBeBlocked() {
        response.then()
                .assertThat()
                .statusCode(403);
        SchemaValidator.validateIfBodyPresent(response, "schemas/unauthorized-error-schema.json");
    }
}

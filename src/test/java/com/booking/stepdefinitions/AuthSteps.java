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
        this.response = bookingClient.getBookingWithoutAuth(999999);
    }

    @Given("a staff member's session has expired")
    public void aStaffMemberSessionHasExpired() {
        this.response = bookingClient.getBookingWithInvalidToken(999999);
    }

    @Given("a staff member's session details are corrupted")
    public void aStaffMemberSessionDetailsAreCorrupted() {
        this.response = bookingClient.getBookingWithMalformedToken(999999);
    }

    @When("they log in")
    public void theyLogIn() {
        response = AuthClient.login(username, password);
    }

    @When("they try to view a booking")
    public void theyTryToViewABooking() {
        // The request was already sent in the Given step based on the session state.
        // This step exists purely for readability in the Gherkin scenario.
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

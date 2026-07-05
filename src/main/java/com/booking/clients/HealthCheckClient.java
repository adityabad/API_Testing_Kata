package com.booking.clients;

import com.booking.specs.SpecFactory;
import com.booking.utils.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class HealthCheckClient {

    public static final String HEALTH_ENDPOINT = ConfigReader.getProperty("health.url");

    /**
     * Calls the booking service health check endpoint to verify the API is up.
     *
     * @return the health check response
     */
    public Response checkHealth() {
        return RestAssured.given()
                .spec(SpecFactory.getUnauthorizedRequestSpecification())
                .when()
                .get(HEALTH_ENDPOINT);
    }
}

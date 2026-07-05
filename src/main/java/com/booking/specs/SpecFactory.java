package com.booking.specs;

import com.booking.clients.AuthClient;
import com.booking.filters.ServerErrorRetryFilter;
import com.booking.utils.ConfigReader;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class SpecFactory {

    private static final ServerErrorRetryFilter RETRY_FILTER = new ServerErrorRetryFilter(3, 1000);
    static String token = AuthClient.getSessionToken();

    public static RequestSpecification getGeneralRequestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getProperty("base.url"))
                .setBasePath("api")
                .addCookie("token", token)
                .setContentType(ContentType.JSON)
                .addFilter(RETRY_FILTER)
                .log(LogDetail.ALL)
                .build();
    }

    /**
     * Returns a request specification without any authentication token.
     * Useful for negative authorization tests.
     */
    public static RequestSpecification getUnauthorizedRequestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getProperty("base.url"))
                .setBasePath("api")
                .setContentType(ContentType.JSON)
                .addFilter(RETRY_FILTER)
                .log(LogDetail.ALL)
                .build();
    }

    /**
     * Returns a request specification with an invalid/expired authentication token.
     * Useful for negative authorization tests.
     */
    public static RequestSpecification getInvalidTokenRequestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getProperty("base.url"))
                .setBasePath("api")
                .addCookie("token", "invalid-expired-token")
                .setContentType(ContentType.JSON)
                .addFilter(RETRY_FILTER)
                .log(LogDetail.ALL)
                .build();
    }

    /**
     * Returns a request specification with a malformed authentication token.
     * Useful for negative authorization tests.
     */
    public static RequestSpecification getMalformedTokenRequestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getProperty("base.url"))
                .setBasePath("api")
                .addCookie("token", "@@@malformed token@@@")
                .setContentType(ContentType.JSON)
                .addFilter(RETRY_FILTER)
                .log(LogDetail.ALL)
                .build();
    }
}

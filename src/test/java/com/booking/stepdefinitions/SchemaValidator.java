package com.booking.stepdefinitions;

import io.restassured.response.Response;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public final class SchemaValidator {

    private SchemaValidator() {
        // utility class
    }

    /**
     * Validates the response body against the given JSON schema if the body is non-empty.
     * Useful for error responses that may return an empty body in some environments.
     */
    public static void validateIfBodyPresent(Response response, String schemaClasspathPath) {
        String body = response.getBody().asString();
        if (body != null && !body.trim().isEmpty()) {
            response.then().body(matchesJsonSchemaInClasspath(schemaClasspathPath));
        }
    }
}

package com.booking.clients;

import com.booking.utils.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

public class AuthClient {

    private static String sessionToken = null;
    public static final String AUTH_ENDPOINT = ConfigReader.getProperty("auth.url");

    /**
     * Authenticates with Restful-Booker Platform and caches the session cookie.
     */
    public static synchronized String getSessionToken() {
        if (sessionToken == null) {
            // Build the body payload explicitly expected by the site
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", ConfigReader.getProperty("username"));
            credentials.put("password", ConfigReader.getProperty("password"));

            Response response = RestAssured.given()
                    .baseUri(ConfigReader.getProperty("base.url"))
                    .contentType(ContentType.JSON)
                    .body(credentials)
                    .when()
                    .post(AUTH_ENDPOINT); // Endpoint specifically for this playground

            // Validate status code 200 before reading response text
            if (response.getStatusCode() != 200) {
                System.out.println("Auth Error Raw Body: " + response.asString());
                throw new RuntimeException("Authentication failed! Verify credentials inside your properties sheet.");
            }

            // The site returns the token in the response body as JSON { "token": "..." }
            sessionToken = response.jsonPath().getString("token");
        }
        return sessionToken;
    }
}

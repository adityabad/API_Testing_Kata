package com.booking.clients;

import com.booking.models.BookingRequest;
import com.booking.utils.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import com.booking.specs.SpecFactory;

import java.util.Map;

public class BookingClient {
    public static final String BOOKING_ENDPOINT = ConfigReader.getProperty("booking.url");

    public Response createBooking(BookingRequest bookingRequest) {
        return RestAssured.given()
                .spec(SpecFactory.getGeneralRequestSpecification())
                .body(bookingRequest)
                .when()
                .post(BOOKING_ENDPOINT);
    }

    public Response getBooking(int bookingId) {
        return RestAssured.given()
                .spec(SpecFactory.getGeneralRequestSpecification())
                .when()
                .get(BOOKING_ENDPOINT + "/" + bookingId);
    }

    public Response updateBooking(int bookingId, BookingRequest bookingRequest) {
        return RestAssured.given()
                .spec(SpecFactory.getGeneralRequestSpecification())
                .body(bookingRequest)
                .when()
                .put(BOOKING_ENDPOINT + "/" + bookingId);
    }

    public Response patchBooking(int bookingId, Map<String, Object> updates) {
        return RestAssured.given()
                .spec(SpecFactory.getGeneralRequestSpecification())
                .body(updates)
                .when()
                .patch(BOOKING_ENDPOINT + "/" + bookingId);
    }

    public Response deleteBooking(int bookingId) {
        return RestAssured.given()
                .spec(SpecFactory.getGeneralRequestSpecification())
                .when()
                .delete(BOOKING_ENDPOINT + "/" + bookingId);
    }

    public Response getBookingWithoutAuth(int bookingId) {
        return RestAssured.given()
                .spec(SpecFactory.getUnauthorizedRequestSpecification())
                .when()
                .get(BOOKING_ENDPOINT + "/" + bookingId);
    }

    public Response getBookingWithInvalidToken(int bookingId) {
        return RestAssured.given()
                .spec(SpecFactory.getInvalidTokenRequestSpecification())
                .when()
                .get(BOOKING_ENDPOINT + "/" + bookingId);
    }
}

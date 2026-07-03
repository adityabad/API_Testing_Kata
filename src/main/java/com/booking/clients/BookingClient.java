package com.booking.clients;

import com.booking.models.BookingRequest;
import com.booking.utils.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import com.booking.specs.SpecFactory;

public class BookingClient {
    public static final String BOOKING_ENDPOINT = ConfigReader.getProperty("booking.url");

    public Response createBooking(BookingRequest bookingRequest) {
        return RestAssured.given().
                    spec(SpecFactory.getGeneralRequestSpecification()).body(bookingRequest).when().post(BOOKING_ENDPOINT);
    }
}

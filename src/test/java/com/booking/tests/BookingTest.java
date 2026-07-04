package com.booking.tests;

import com.booking.clients.BookingClient;
import com.booking.models.BookingDates;
import com.booking.models.BookingRequest;
import com.booking.models.BookingResponse;
import io.restassured.response.Response;

import org.testng.annotations.Test;


public class BookingTest {
    private final BookingClient bookingClient = new BookingClient();
    private static int newRoomId() {
        return (int)(Math.random() * 1000);
    }
    @Test
    public void createBookingTest() {
        BookingDates bookingDates =  BookingDates.builder().checkin("2026-08-16").checkout("2026-08-17").build();
        BookingRequest bookingRequest =  BookingRequest.builder().phone("99888888888").email("asdfafk@dv.co").roomid(newRoomId()).firstname("dddd").lastname("lll").depositpaid(true).bookingdates(bookingDates).build();
        Response response = bookingClient.createBooking(bookingRequest);
        BookingResponse bookingResponse = response.as(BookingResponse.class);
        response.then().log().all().assertThat().statusCode(201);







    }
}

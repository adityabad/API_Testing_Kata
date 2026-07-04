package com.booking.context;

import io.restassured.response.Response;

/**
 * Shared scenario context used by Cucumber step definitions.
 * <p>
 * Cucumber PicoContainer creates one instance per scenario, so this class is
 * naturally thread-safe when tests run in parallel.
 */
public class BookingContext {

    private Response response;
    private int bookingId;
    private boolean bookingCreated;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public boolean isBookingCreated() {
        return bookingCreated;
    }

    public void setBookingCreated(boolean bookingCreated) {
        this.bookingCreated = bookingCreated;
    }

    public void reset() {
        this.response = null;
        this.bookingId = 0;
        this.bookingCreated = false;
    }
}

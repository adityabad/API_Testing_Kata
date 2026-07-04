package com.booking.context;

import com.booking.models.BookingRequest;
import io.restassured.response.Response;

import java.util.Map;

/**
 * Shared scenario context used by Cucumber step definitions.
 * <p>
 * Cucumber creates one instance of the step definition class per scenario,
 * so this context is naturally isolated when tests run in parallel.
 */
public class BookingContext {

    private Response response;
    private int bookingId;
    private boolean bookingCreated;
    private BookingRequest lastBookingRequest;
    private Map<String, Object> lastPartialUpdate;

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

    public BookingRequest getLastBookingRequest() {
        return lastBookingRequest;
    }

    public void setLastBookingRequest(BookingRequest lastBookingRequest) {
        this.lastBookingRequest = lastBookingRequest;
    }

    public Map<String, Object> getLastPartialUpdate() {
        return lastPartialUpdate;
    }

    public void setLastPartialUpdate(Map<String, Object> lastPartialUpdate) {
        this.lastPartialUpdate = lastPartialUpdate;
    }

    public void reset() {
        this.response = null;
        this.bookingId = 0;
        this.bookingCreated = false;
        this.lastBookingRequest = null;
        this.lastPartialUpdate = null;
    }
}

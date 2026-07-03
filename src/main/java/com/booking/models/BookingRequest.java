package com.booking.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingRequest {
    private int roomid; // Note: Restful-Booker playground expects lowercase 'roomid'
    private String firstname; // Note: Playground expects lowercase 'firstname'
    private String lastname;  // Note: Playground expects lowercase 'lastname'
    private boolean depositpaid;
    private String email;
    private String phone;
    private BookingDates bookingdates;
}
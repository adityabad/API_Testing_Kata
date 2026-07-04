package com.booking.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingDetails {
    int roomId;
    String firstName;
    String lastName;
    boolean depositpaid;
    BookingDates bookingdates;
}
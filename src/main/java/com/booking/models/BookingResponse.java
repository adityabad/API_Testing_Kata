package com.booking.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingResponse {
    String bookingid;
    String email;
    String phone;
    BookingDetails bookingdetails;
}


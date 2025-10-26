package com.rideshare.controller;

import com.rideshare.entity.Booking;
import com.rideshare.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    // NOTE: This logic assumes you would also implement RideRepository/Service logic
    // to find the ride and reduce its availableSeats in a real application.

    @PostMapping("/book")
    public String bookSeat(@RequestBody Booking booking) {
        // Feature 5: Confirmation logic (Simulation)
        if (booking.getSeatsBooked() > 4 || booking.getSeatsBooked() <= 0) {
            return "Invalid number of seats requested.";
        }

        booking.setStatus("CONFIRMED");
        bookingRepository.save(booking);

        return "Booking confirmed! You have successfully reserved " +
                booking.getSeatsBooked() + " seat(s) for Ride ID " + booking.getRideId() + ".";

    }
}
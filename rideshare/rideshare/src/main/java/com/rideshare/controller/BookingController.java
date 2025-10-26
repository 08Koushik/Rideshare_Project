package com.rideshare.controller;

import com.rideshare.entity.Booking;
import com.rideshare.entity.Ride;
import com.rideshare.repository.BookingRepository;
import com.rideshare.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RideRepository rideRepository;

    // ✅ Endpoint: Book a seat
    @PostMapping("/book")
    public String bookRide(@RequestBody Booking bookingRequest) {
        Long rideId = bookingRequest.getRideId();
        int seatsRequested = bookingRequest.getSeatsBooked();

        // 1️⃣ Find the ride by ID
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // 2️⃣ Check if enough seats are available
        if (ride.getAvailableSeats() < seatsRequested) {
            return "Booking failed: Not enough seats available.";
        }

        // 3️⃣ Deduct the booked seats
        ride.setAvailableSeats(ride.getAvailableSeats() - seatsRequested);
        rideRepository.save(ride); // update seats

        // 4️⃣ Save the booking record
        bookingRepository.save(bookingRequest);

        // 5️⃣ Return success message
        return "Booking successful! Remaining seats: " + ride.getAvailableSeats();
    }
}

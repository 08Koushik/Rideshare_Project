package com.rideshare.service;

import com.rideshare.dto.DriverRideRequestDTO;
import com.rideshare.entity.Booking;
import com.rideshare.entity.Ride;
import com.rideshare.entity.User;
import com.rideshare.repository.BookingRepository;
import com.rideshare.repository.RideRepository;
import com.rideshare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Fetches all relevant ride requests (Bookings) for a specific driver.
     * Joins Booking, Ride, and Passenger (User) details.
     */
    public List<DriverRideRequestDTO> getRideRequestsForDriver(Long driverId) {
        // 1. Find all rides posted by this driver
        List<Ride> driverRides = rideRepository.findByDriverId(driverId);
        List<Long> rideIds = driverRides.stream().map(Ride::getId).collect(Collectors.toList());

        if (rideIds.isEmpty()) {
            return List.of(); // Return empty list if the driver has no rides
        }

        // 2. Find all bookings associated with these rides
        List<Booking> bookings = bookingRepository.findByRideIdIn(rideIds);

        // 3. Map bookings to DTOs, fetching passenger and ride data
        return bookings.stream().map(booking -> {
            // Find the associated Ride and Passenger
            Ride ride = driverRides.stream()
                    .filter(r -> r.getId().equals(booking.getRideId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Ride data mismatch"));

            // Fetch Passenger (User) details
            User passenger = userRepository.findById(booking.getPassengerId())
                    .orElseThrow(() -> new RuntimeException("Passenger not found"));

            return new DriverRideRequestDTO(
                    booking.getId(),
                    ride.getId(),
                    passenger.getName(),
                    passenger.getContactNumber(),
                    ride.getSource(),
                    ride.getDestination(),
                    booking.getSeatsBooked(),
                    booking.getStatus()
            );
        }).collect(Collectors.toList());
    }

    /**
     * Updates the status of a specific booking (e.g., REQUESTED -> ACCEPTED/DENIED).
     */
    public Booking updateBookingStatus(Long bookingId, String newStatus) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found!"));

        // In a real application, you would validate the status (e.g., ensure it's REQUESTED, ACCEPTED, or DENIED)
        booking.setStatus(newStatus.toUpperCase());
        return bookingRepository.save(booking);
    }
}
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest; // Required for manual Pageable creation
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort; // Required for Pageable Sort

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    /**
     * MODIFIED: Fetches all relevant ride requests (Bookings) for a specific driver, PAGINATED.
     */
    public Page<DriverRideRequestDTO> getRideRequestsForDriver(Long driverId, Pageable pageable) {

        // 1. Find all rides posted by this driver (fetching ALL records to find all ride IDs)
        // Use a large size and Page 0 to fetch all records from the paginated repository method.
        Pageable allRidesPageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.unsorted());
        List<Ride> driverRides = rideRepository.findByDriverId(driverId, allRidesPageable).getContent();

        List<Long> rideIds = driverRides.stream().map(Ride::getId).collect(Collectors.toList());

        if (rideIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0); // Return empty page
        }

        // 2. Find all bookings associated with these rides (NOT PAGINATED YET)
        List<Booking> allBookings = bookingRepository.findByRideIdIn(rideIds);

        // 3. Map to DTOs and filter/sort/paginate manually based on the fetched list
        List<DriverRideRequestDTO> allDtos = allBookings.stream().map(booking -> {
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

        // 4. Manual Pagination Logic for the DTO list:
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allDtos.size());
        List<DriverRideRequestDTO> pageContent;

        if (start < allDtos.size()) {
            pageContent = allDtos.subList(start, end);
        } else {
            // Handle case where start index is out of bounds (shouldn't happen with proper frontend controls)
            pageContent = List.of();
        }

        return new PageImpl<>(pageContent, pageable, allDtos.size());
    }

    /**
     * Updates the status of a specific booking (e.g., REQUESTED -> ACCEPTED/DENIED/CANCELLED/RESCHEDULED).
     */
    public Booking updateBookingStatus(Long bookingId, String newStatus, String newDateTime) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found!"));

        Ride ride = rideRepository.findById(booking.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride not found!"));

        User passenger = userRepository.findById(booking.getPassengerId())
                .orElseThrow(() -> new RuntimeException("Passenger not found!"));

        User driver = userRepository.findById(ride.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found!"));

        String oldStatus = booking.getStatus();
        booking.setStatus(newStatus.toUpperCase());

        // --- LOGIC BASED ON NEW STATUS ---
        if (newStatus.equalsIgnoreCase("CANCELLED") && !oldStatus.equalsIgnoreCase("CANCELLED")) {
            // 1. Return seats to the ride pool
            ride.setAvailableSeats(ride.getAvailableSeats() + booking.getSeatsBooked());
            rideRepository.save(ride);

            // 2. Notify passenger of cancellation
            emailService.sendBookingCancellation(
                    passenger.getEmail(),
                    passenger.getName(),
                    driver.getName(),
                    ride.getSource(),
                    ride.getDestination()
            );

        } else if (newStatus.equalsIgnoreCase("RESCHEDULED") && newDateTime != null) {
            // Update the ride's date/time in the database, affecting all current bookings on this ride.
            LocalDateTime parsedDateTime = LocalDateTime.parse(newDateTime);
            ride.setDateTime(parsedDateTime);
            rideRepository.save(ride);

            // Notify passenger of reschedule
            emailService.sendBookingReschedule(
                    passenger.getEmail(),
                    passenger.getName(),
                    driver.getName(),
                    ride.getSource(),
                    ride.getDestination(),
                    parsedDateTime.toString()
            );
        } else if (newStatus.equalsIgnoreCase("ACCEPTED")) {
            // Explicitly send email upon driver acceptance
            double totalAmount = ride.getFarePerSeat() * booking.getSeatsBooked();

            emailService.sendBookingConfirmation(
                    passenger.getEmail(),
                    passenger.getName(),
                    driver.getName(),
                    ride.getSource(),
                    ride.getDestination(),
                    ride.getDateTime().toString(),
                    booking.getSeatsBooked(),
                    totalAmount
            );
        }

        return bookingRepository.save(booking);
    }
}
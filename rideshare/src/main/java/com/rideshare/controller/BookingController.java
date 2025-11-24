package com.rideshare.controller;

import com.rideshare.entity.Booking;
import com.rideshare.entity.Ride;
import com.rideshare.entity.User;
import com.rideshare.repository.BookingRepository;
import com.rideshare.repository.RideRepository;
import com.rideshare.repository.UserRepository;
import com.rideshare.service.PaymentService;
import com.rideshare.service.BookingService;
import com.rideshare.service.EmailService;
import com.rideshare.dto.DriverRideRequestDTO;
import com.rideshare.dto.ClientSecretResponse;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ---------------- 1. CREATE PAYMENT INTENT BEFORE BOOKING ----------------
    @PostMapping("/create-intent")
    public ClientSecretResponse createPaymentIntent(@RequestParam Long rideId,
                                                    @RequestParam int seats) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (ride.getAvailableSeats() < seats)
            throw new RuntimeException("Not enough seats available.");

        double totalAmount = ride.getFarePerSeat() * seats;

        String clientSecret = paymentService.createPaymentIntent(totalAmount);
        return new ClientSecretResponse(clientSecret);
    }

    // ---------------- 2. FINAL BOOKING CONFIRMATION ----------------
    @PostMapping("/confirm")
    public String confirmBooking(@RequestBody Booking bookingRequest,
                                 @RequestParam String paymentIntentId) {

        Long rideId = bookingRequest.getRideId();
        int seatsRequested = bookingRequest.getSeatsBooked();
        bookingRequest.setStatus("REQUESTED");

        // Retrieve ride
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // Retrieve passenger + driver
        User passenger = userRepository.findById(bookingRequest.getPassengerId())
                .orElseThrow(() -> new RuntimeException("Passenger not found"));

        User driver = userRepository.findById(ride.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        // Seat check
        if (ride.getAvailableSeats() < seatsRequested)
            throw new RuntimeException("Not enough seats available.");

        // Retrieve Stripe PaymentIntent
        PaymentIntent intent = paymentService.retrievePaymentIntent(paymentIntentId);

        if (!"succeeded".equals(intent.getStatus())) {
            return "Payment FAILED or not completed. Status = " + intent.getStatus();
        }

        // Save booking after payment confirmation
        Booking saved = bookingRepository.save(bookingRequest);

        // Reduce seats
        ride.setAvailableSeats(ride.getAvailableSeats() - seatsRequested);
        rideRepository.save(ride);

        // Real-time update to driver
        messagingTemplate.convertAndSend(
                "/topic/driver/" + driver.getId() + "/updates",
                "New booking! Seats: " + seatsRequested
        );

        // Send email
        emailService.sendBookingConfirmation(
                passenger.getEmail(),
                passenger.getName(),
                driver.getName(),
                ride.getSource(),
                ride.getDestination(),
                ride.getDateTime().toString(),
                seatsRequested,
                ride.getFarePerSeat() * seatsRequested
        );

        return "Booking confirmed, Payment successful!";
    }

    // ---------------- DRIVER REQUESTS ----------------
    @GetMapping("/driver/{driverId}/requests")
    public List<DriverRideRequestDTO> getDriverRequests(@PathVariable Long driverId) {
        return bookingService.getRideRequestsForDriver(driverId);
    }

    // ---------------- ADMIN VIEW ----------------
    @GetMapping("/admin/bookings")
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}

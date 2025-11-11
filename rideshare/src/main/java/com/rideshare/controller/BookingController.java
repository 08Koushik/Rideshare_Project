package com.rideshare.controller;

import com.rideshare.entity.Booking;
import com.rideshare.entity.Ride;
import com.rideshare.entity.User; // NEW IMPORT
import com.rideshare.repository.BookingRepository;
import com.rideshare.repository.RideRepository;
import com.rideshare.repository.UserRepository; // NEW IMPORT
import com.rideshare.service.PaymentService;
import com.rideshare.service.BookingService;
import com.rideshare.service.EmailService; // NEW IMPORT
import com.rideshare.dto.DriverRideRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.List;
import com.rideshare.dto.ClientSecretResponse;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository; // NEW INJECTION

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private EmailService emailService; // NEW INJECTION

    @PostMapping("/book")
    public String bookRide(@RequestBody Booking bookingRequest, @RequestParam String paymentMethodId) {
        Long rideId = bookingRequest.getRideId();
        int seatsRequested = bookingRequest.getSeatsBooked();
        bookingRequest.setStatus("REQUESTED");

        // 1️⃣ Find the ride by ID
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // 1.5 Fetch Passenger and Driver details early for email
        User passenger = userRepository.findById(bookingRequest.getPassengerId())
                .orElseThrow(() -> new RuntimeException("Passenger not found."));

        User driver = userRepository.findById(ride.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found for ride."));


        // 2️⃣ Check if enough seats are available
        if (ride.getAvailableSeats() < seatsRequested) {
            throw new RuntimeException("Booking failed: Not enough seats available.");
        }

        // 3️⃣ CALCULATE PAYMENT AMOUNT
        double farePerSeat = ride.getFarePerSeat();
        double totalAmount = farePerSeat * seatsRequested;

        // Set initial status and save booking record
        Booking savedBooking = bookingRepository.save(bookingRequest);

        // 5️⃣ PROCESS PAYMENT VIA STRIPE (Calls the 5-argument method)
        String paymentStatusMessage = paymentService.createStripePaymentIntentAndConfirm(
                savedBooking.getId(),
                bookingRequest.getPassengerId(),
                totalAmount,
                rideId,
                paymentMethodId // Passes the required payment method ID
        );

        // 6️⃣ Deduct seats & save Ride update
        ride.setAvailableSeats(ride.getAvailableSeats() - seatsRequested);
        rideRepository.save(ride);

        // 7️⃣ Send Real-Time Notification to Driver
        String driverTopic = "/topic/driver/" + ride.getDriverId() + "/updates";
        String message = String.format("NEW BOOKING! Ride %d has %d new seat(s) booked. %d seats remaining.",
                rideId, seatsRequested, ride.getAvailableSeats());

        messagingTemplate.convertAndSend(driverTopic, message);

        // 🆕 NEW LOGIC: Send booking confirmation email to passenger
        emailService.sendBookingConfirmation(
                passenger.getEmail(),
                passenger.getName(),
                driver.getName(),
                ride.getSource(),
                ride.getDestination(),
                ride.getDateTime().toString(), // Use toString for simplicity
                seatsRequested,
                totalAmount
        );
        // --------------------------------------------------------

        // 8️⃣ Simulate Driver Payout (post-completion requirement)
        paymentService.processDriverPayout(ride.getDriverId(), totalAmount);

        // 9️⃣ Return success message
        return "Booking successful! " + paymentStatusMessage;
    }

    // NEW ENDPOINT: Fetch all requests/bookings for a specific driver
    @GetMapping("/driver/{driverId}/requests")
    public List<DriverRideRequestDTO> getDriverRequests(@PathVariable Long driverId) {
        return bookingService.getRideRequestsForDriver(driverId);
    }

    // NEW ENDPOINT: Update the status of a specific booking (Accept/Deny)
    @PostMapping("/{bookingId}/status")
    public Booking updateRequestStatus(@PathVariable Long bookingId, @RequestParam String status) {
        return bookingService.updateBookingStatus(bookingId, status);
    }

    @PostMapping("/create-intent")
    public ClientSecretResponse createPaymentIntent(@RequestParam Long rideId, @RequestParam int seats) {
        // 1. Find the ride and calculate the total amount
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (ride.getAvailableSeats() < seats) {
            throw new RuntimeException("Booking failed: Not enough seats available.");
        }

        double totalAmount = ride.getFarePerSeat() * seats;

        // 2. Call the service method to create the intent and get the secret
        String clientSecret = paymentService.createPaymentIntent(totalAmount);

        return new ClientSecretResponse(clientSecret);
    }
    @GetMapping("/admin/bookings")
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}
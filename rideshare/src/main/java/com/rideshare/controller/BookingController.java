package com.rideshare.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // NEW IMPORT
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // NEW IMPORT
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping; // NEW IMPORT
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rideshare.dto.ClientSecretResponse;
import com.rideshare.dto.DriverRideRequestDTO;
import com.rideshare.dto.PassengerBookingDTO;
import com.rideshare.entity.Booking;
import com.rideshare.entity.Ride;
import com.rideshare.entity.User;
import com.rideshare.repository.BookingRepository;
import com.rideshare.repository.RideRepository;
import com.rideshare.repository.UserRepository;
import com.rideshare.service.BookingService;
import com.rideshare.service.EmailService;
import com.rideshare.service.PaymentService;

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
    public String bookRide(@RequestBody Booking bookingRequest) {
        Long rideId = bookingRequest.getRideId();
        int seatsRequested = bookingRequest.getSeatsBooked();
        bookingRequest.setStatus("PENDING"); // Status set to PENDING awaiting driver approval

        // 1️⃣ Find the ride by ID
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // 2️⃣ Fetch Passenger and Driver details
        User passenger = userRepository.findById(bookingRequest.getPassengerId())
                .orElseThrow(() -> new RuntimeException("Passenger not found."));

        User driver = userRepository.findById(ride.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found for ride."));

        // 3️⃣ Check if enough seats are available
        if (ride.getAvailableSeats() < seatsRequested) {
            throw new RuntimeException("Booking failed: Not enough seats available.");
        }

        // 4️⃣ Calculate fare (for information only, no payment yet)
        double farePerSeat = ride.getFarePerSeat();
        double totalAmount = farePerSeat * seatsRequested;

        // 5️⃣ Save booking request with PENDING status
        Booking savedBooking = bookingRepository.save(bookingRequest);

        // 6️⃣ Send email notification to driver about new booking request
        emailService.sendBookingRequestToDriver(
            driver.getEmail(),
            driver.getName(),
            passenger.getName(),
            ride.getSource(),
            ride.getDestination(),
            ride.getDateTime().toString(),
            seatsRequested,
            totalAmount,
            savedBooking.getId()
        );

        // 7️⃣ Send Real-Time Notification to Driver
        String driverTopic = "/topic/driver/" + ride.getDriverId() + "/updates";
        String message = String.format("NEW BOOKING REQUEST! %s wants to book %d seat(s) for ride from %s to %s.",
                passenger.getName(), seatsRequested, ride.getSource(), ride.getDestination());

        messagingTemplate.convertAndSend(driverTopic, message);

        // 8️⃣ Return success message
        return "Booking request sent! Waiting for driver approval.";
    }

    // NEW ENDPOINT: Fetch all requests/bookings for a specific driver
    @GetMapping("/driver/{driverId}/requests")
    public List<DriverRideRequestDTO> getDriverRequests(@PathVariable Long driverId) {
        return bookingService.getRideRequestsForDriver(driverId);
    }

    // NEW ENDPOINT: Fetch all bookings for a specific passenger
    @GetMapping("/passenger/{passengerId}/bookings")
    public List<PassengerBookingDTO> getPassengerBookings(@PathVariable Long passengerId) {
        return bookingService.getBookingsForPassenger(passengerId);
    }

    // NEW ENDPOINT: Update the status of a specific booking (Accept/Deny)
    @PostMapping("/{bookingId}/status")
    public ResponseEntity<String> updateRequestStatus(
            @PathVariable Long bookingId, 
            @RequestParam String status
    ) {
        // 1️⃣ Fetch Booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found."));

        // 2️⃣ Fetch Ride
        Ride ride = rideRepository.findById(booking.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride not found."));

        // 3️⃣ Fetch Passenger
        User passenger = userRepository.findById(booking.getPassengerId())
                .orElseThrow(() -> new RuntimeException("Passenger not found."));

        // 4️⃣ Update Status
        booking.setStatus(status);
        bookingRepository.save(booking);

        // 5️⃣ If APPROVED, deduct seats and notify passenger
        if ("APPROVED".equalsIgnoreCase(status)) {
            // Check if enough seats available
            if (ride.getAvailableSeats() < booking.getSeatsBooked()) {
                booking.setStatus("REJECTED");
                bookingRepository.save(booking);
                
                emailService.sendBookingStatusUpdate(
                    passenger.getEmail(),
                    passenger.getName(),
                    bookingId,
                    "REJECTED",
                    "Sorry, not enough seats available anymore."
                );
                
                return ResponseEntity.badRequest().body("Not enough seats available");
            }

            // Deduct seats
            ride.setAvailableSeats(ride.getAvailableSeats() - booking.getSeatsBooked());
            rideRepository.save(ride);

            // Calculate fare for information
            double farePerSeat = ride.getFarePerSeat();
            double totalAmount = farePerSeat * booking.getSeatsBooked();

            // Send approval email to passenger
            emailService.sendBookingStatusUpdate(
                passenger.getEmail(),
                passenger.getName(),
                bookingId,
                "APPROVED",
                String.format("Your booking has been approved!\n\nRide Details:\nFrom: %s\nTo: %s\nDate & Time: %s\nSeats: %d\nFare: ₹%.2f\n\nPlease confirm your details and be ready for the ride.",
                    ride.getSource(),
                    ride.getDestination(),
                    ride.getDateTime().toString(),
                    booking.getSeatsBooked(),
                    totalAmount
                )
            );

            return ResponseEntity.ok("Booking approved. Passenger notified via email.");

        } else if ("REJECTED".equalsIgnoreCase(status)) {
            // Send rejection email to passenger
            emailService.sendBookingStatusUpdate(
                passenger.getEmail(),
                passenger.getName(),
                bookingId,
                "REJECTED",
                "The driver has declined your booking request. Please search for another ride."
            );
            return ResponseEntity.ok("Booking rejected. Passenger notified.");
        }

        return ResponseEntity.ok("Booking status updated to " + status);
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
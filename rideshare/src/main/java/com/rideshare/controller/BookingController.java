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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.List;
import com.rideshare.dto.ClientSecretResponse;
import org.springframework.data.domain.Page; // NEW IMPORT
import org.springframework.data.domain.Pageable; // NEW IMPORT
import org.springframework.data.domain.PageRequest;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository; //

    @Autowired
    private RideRepository rideRepository; //

    @Autowired
    private UserRepository userRepository; //

    @Autowired
    private PaymentService paymentService; //

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private BookingService bookingService; //

    @Autowired
    private EmailService emailService; //

    @PostMapping("/book")
    public String bookRide(@RequestBody Booking bookingRequest, @RequestParam String paymentMethodId) {
        Long rideId = bookingRequest.getRideId();
        int seatsRequested = bookingRequest.getSeatsBooked();
        bookingRequest.setStatus("REQUESTED");


        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found")); //

        User passenger = userRepository.findById(bookingRequest.getPassengerId())
                .orElseThrow(() -> new RuntimeException("Passenger not found.")); //


        if (passenger.isBlocked()) { //
            throw new RuntimeException("Booking failed: Your account is currently blocked.");
        }


        User driver = userRepository.findById(ride.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found for ride.")); //



        if (ride.getAvailableSeats() < seatsRequested) {
            throw new RuntimeException("Booking failed: Not enough seats available.");
        }

        double farePerSeat = ride.getFarePerSeat();
        double totalAmount = farePerSeat * seatsRequested;


        Booking savedBooking = bookingRepository.save(bookingRequest);


        String paymentStatusMessage = paymentService.createStripePaymentIntentAndConfirm(
                savedBooking.getId(),
                bookingRequest.getPassengerId(),
                totalAmount,
                rideId,
                paymentMethodId
        );


        ride.setAvailableSeats(ride.getAvailableSeats() - seatsRequested);
        rideRepository.save(ride); //


        String driverTopic = "/topic/driver/" + ride.getDriverId() + "/updates";
        String message = String.format("NEW BOOKING! Ride %d has %d new seat(s) booked. %d seats remaining.",
                rideId, seatsRequested, ride.getAvailableSeats());

        messagingTemplate.convertAndSend(driverTopic, message);


        emailService.sendBookingConfirmation( //
                passenger.getEmail(),
                passenger.getName(),
                driver.getName(),
                ride.getSource(),
                ride.getDestination(),
                ride.getDateTime().toString(),
                seatsRequested,
                totalAmount
        );

        paymentService.processDriverPayout(ride.getDriverId(), totalAmount); //


        return "Booking successful! " + paymentStatusMessage;
    }

    @GetMapping("/driver/{driverId}/requests")
    public Page<DriverRideRequestDTO> getDriverRequests(
            @PathVariable Long driverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return bookingService.getRideRequestsForDriver(driverId, pageable);
    }

    @PostMapping("/{bookingId}/status")
    public Booking updateRequestStatus(
            @PathVariable Long bookingId,
            @RequestParam String status,
            // NEW OPTIONAL PARAMETER for Reschedule date/time
            @RequestParam(required = false) String newDateTime
    ) {
        return bookingService.updateBookingStatus(bookingId, status, newDateTime);
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


        String clientSecret = paymentService.createPaymentIntent(totalAmount);

        return new ClientSecretResponse(clientSecret);
    }
    @GetMapping("/admin/bookings")
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

}
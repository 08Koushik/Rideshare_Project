package com.rideshare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rideshare.entity.User;

@Service
public class NotificationService {

    @Autowired
    private EmailService emailService;

    /**
     * Send booking confirmation to passenger
     */
    public void sendBookingConfirmation(User passenger, String rideDetails) {
        // Send email notification
        String subject = "Booking Confirmed - Rideshare";
        String message = String.format(
            "Dear %s,\n\n" +
            "Your ride booking has been confirmed!\n\n" +
            "Ride Details:\n%s\n\n" +
            "Thank you for using Rideshare!\n\n" +
            "Best regards,\n" +
            "Rideshare Team",
            passenger.getName(),
            rideDetails
        );
        emailService.sendSimpleEmail(passenger.getEmail(), subject, message);

        // SMS functionality disabled - User entity doesn't have phone field
        // If you want to enable SMS, add a phone field to the User entity
    }

    /**
     * Send booking notification to driver
     */
    public void sendBookingNotificationToDriver(User driver, String bookingDetails) {
        // Send email notification
        String subject = "New Booking Request - Rideshare";
        String message = String.format(
            "Dear %s,\n\n" +
            "You have a new booking request!\n\n" +
            "Booking Details:\n%s\n\n" +
            "Please review and respond to the booking.\n\n" +
            "Best regards,\n" +
            "Rideshare Team",
            driver.getName(),
            bookingDetails
        );
        emailService.sendSimpleEmail(driver.getEmail(), subject, message);

        // SMS functionality disabled - User entity doesn't have phone field
    }

    /**
     * Send booking status update to passenger
     */
    public void sendBookingStatusUpdate(User passenger, String status, String rideDetails) {
        String subject = "Booking Status Update - Rideshare";
        String message = String.format(
            "Dear %s,\n\n" +
            "Your booking status has been updated to: %s\n\n" +
            "Ride Details:\n%s\n\n" +
            "Thank you for using Rideshare!\n\n" +
            "Best regards,\n" +
            "Rideshare Team",
            passenger.getName(),
            status,
            rideDetails
        );
        emailService.sendSimpleEmail(passenger.getEmail(), subject, message);

        // SMS functionality disabled - User entity doesn't have phone field
    }

    /**
     * Send payment confirmation
     */
    public void sendPaymentConfirmation(User user, String paymentDetails) {
        String subject = "Payment Confirmation - Rideshare";
        String message = String.format(
            "Dear %s,\n\n" +
            "Your payment has been processed successfully!\n\n" +
            "Payment Details:\n%s\n\n" +
            "Thank you for using Rideshare!\n\n" +
            "Best regards,\n" +
            "Rideshare Team",
            user.getName(),
            paymentDetails
        );
        emailService.sendSimpleEmail(user.getEmail(), subject, message);
    }
}

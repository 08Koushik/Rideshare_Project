package com.rideshare.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Use the username from application.properties as the sender's email
    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendTemporaryPassword(String toEmail, String name, String tempPassword) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome to RideShare - Your Temporary Password");

        String body = String.format(
                "Hello %s,\n\n" +
                        "Your RideShare account has been created by the Admin.\n\n" +
                        "Your temporary password is: %s\n\n" +
                        "Please log in and reset your password immediately for security.\n\n" +
                        "Thank you,\n" +
                        "The RideShare Team",
                name, tempPassword
        );

        message.setText(body);

        try {
            mailSender.send(message);
            System.out.println("Temporary password email sent to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Error sending email to " + toEmail + ": " + e.getMessage());
        }
    }

    // NEW METHOD 1: Driver Ride Post Confirmation
    public void sendRidePostConfirmation(String toEmail, String driverName, String source, String destination, String dateTime, double fare) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(toEmail);
        message.setSubject("Ride Posted Successfully - RideShare");

        String body = String.format(
                "Hello %s,\n\n" +
                        "Your ride has been successfully posted!\n\n" +
                        "Details:\n" +
                        "  Route: %s to %s\n" +
                        "  Date & Time: %s\n" +
                        "  Fare Per Seat: ₹%.2f\n\n" +
                        "You will receive a notification when a passenger books a seat.\n\n" +
                        "Thank you for sharing the ride,\n" +
                        "The RideShare Team",
                driverName, source, destination, dateTime, fare
        );

        message.setText(body);

        try {
            mailSender.send(message);
            System.out.println("Ride post confirmation email sent to driver: " + toEmail);
        } catch (Exception e) {
            System.err.println("Error sending driver ride confirmation email to " + toEmail + ": " + e.getMessage());
        }
    }

    // NEW METHOD 2: Passenger Booking Confirmation
    public void sendBookingConfirmation(String toEmail, String passengerName, String driverName, String source, String destination, String dateTime, int seats, double amount) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(toEmail);
        message.setSubject("Ride Booking Confirmed - RideShare");

        String body = String.format(
                "Hello %s,\n\n" +
                        "Your ride booking has been successfully confirmed!\n\n" +
                        "Details:\n" +
                        "  Route: %s to %s\n" +
                        "  Date & Time: %s\n" +
                        "  Seats Booked: %d\n" +
                        "  Total Amount Paid: ₹%.2f\n" +
                        "  Driver: %s\n\n" +
                        "Safe travels!\n\n" +
                        "The RideShare Team",
                passengerName, source, destination, dateTime, seats, amount, driverName
        );

        message.setText(body);

        try {
            mailSender.send(message);
            System.out.println("Booking confirmation email sent to passenger: " + toEmail);
        } catch (Exception e) {
            System.err.println("Error sending passenger booking confirmation email to " + toEmail + ": " + e.getMessage());
        }
    }
}
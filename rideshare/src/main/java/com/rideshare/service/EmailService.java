package com.rideshare.service;

// --- ADD THESE THREE IMPORTS ---
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
// -------------------------------

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
}
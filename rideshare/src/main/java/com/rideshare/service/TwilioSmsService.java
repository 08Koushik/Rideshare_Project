package com.rideshare.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.annotation.PostConstruct;

@Service
public class TwilioSmsService {

    @Value("${twilio.account-sid:}")
    private String accountSid;

    @Value("${twilio.auth-token:}")
    private String authToken;

    @Value("${twilio.phone-number:}")
    private String fromPhoneNumber;

    private boolean twilioConfigured = false;

    @PostConstruct
    public void init() {
        if (accountSid != null && !accountSid.isEmpty() && 
            authToken != null && !authToken.isEmpty()) {
            try {
                Twilio.init(accountSid, authToken);
                twilioConfigured = true;
                System.out.println("Twilio SMS service initialized successfully");
            } catch (Exception e) {
                System.err.println("Failed to initialize Twilio: " + e.getMessage());
                twilioConfigured = false;
            }
        } else {
            System.out.println("Twilio credentials not configured - SMS notifications disabled");
        }
    }

    public void sendSms(String toPhoneNumber, String messageBody) {
        if (!twilioConfigured) {
            System.out.println("Twilio not configured - skipping SMS to " + toPhoneNumber);
            return;
        }

        try {
            Message message = Message.creator(
                new PhoneNumber(toPhoneNumber),
                new PhoneNumber(fromPhoneNumber),
                messageBody
            ).create();

            System.out.println("SMS sent successfully. SID: " + message.getSid());
        } catch (Exception e) {
            System.err.println("Failed to send SMS: " + e.getMessage());
        }
    }
}

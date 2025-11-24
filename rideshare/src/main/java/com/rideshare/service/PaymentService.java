package com.rideshare.service;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    // ---------------- CREATE PAYMENT INTENT ----------------
    public String createPaymentIntent(double amountInInr) {

        long amountInPaise = Math.round(amountInInr * 100.0);

        try {
            PaymentIntentCreateParams params =
                    PaymentIntentCreateParams.builder()
                            .setAmount(amountInPaise)
                            .setCurrency("inr")
                            .setAutomaticPaymentMethods(
                                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                            .setEnabled(true)
                                            .build()
                            )
                            .build();

            PaymentIntent intent = PaymentIntent.create(params);
            return intent.getClientSecret();

        } catch (Exception e) {
            throw new RuntimeException("Stripe: failed to create PaymentIntent: " + e.getMessage(), e);
        }
    }

    // ---------------- RETRIEVE EXISTING INTENT (NO CONFIRM) ----------------
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) {
        try {
            return PaymentIntent.retrieve(paymentIntentId);
        } catch (Exception e) {
            throw new RuntimeException("Stripe: failed to retrieve PaymentIntent: " + e.getMessage(), e);
        }
    }

    // ---------------- SIMULATED DRIVER PAYOUT ----------------
    public void processDriverPayout(Long driverId, double amount) {
        System.out.println("Driver payout simulated for driverId=" + driverId + ", amount=" + amount);
    }
}

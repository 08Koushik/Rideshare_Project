package com.rideshare.controller;

import com.rideshare.entity.Payment;
import com.rideshare.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    // Endpoint to get all payments/transactions for a passenger
    @GetMapping("/passenger/{passengerId}")
    public List<Payment> getPassengerHistory(@PathVariable Long passengerId) {
        return paymentRepository.findByPassengerId(passengerId);
    }
    @GetMapping("/admin/payments")
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
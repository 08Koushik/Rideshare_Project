package com.rideshare.controller;

import com.rideshare.entity.Payment;
import com.rideshare.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.data.domain.Page; // NEW IMPORT
import org.springframework.data.domain.Pageable; // NEW IMPORT
import org.springframework.data.domain.PageRequest;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @GetMapping("/passenger/{passengerId}")
    public Page<Payment> getPassengerHistory(
            @PathVariable Long passengerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return paymentRepository.findByPassengerId(passengerId, pageable); //
    }
    @GetMapping("/admin/payments")
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
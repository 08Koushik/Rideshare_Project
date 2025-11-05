package com.rideshare.repository;

import com.rideshare.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Custom methods can be added here later (e.g., findByPassengerId)
    List<Payment> findByPassengerId(Long passengerId);
}
package com.rideshare.repository;

import com.rideshare.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findByPassengerId(Long passengerId, Pageable pageable);
}
package com.rideshare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rideshare.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByRideIdIn(List<Long> rideIds);
    List<Booking> findByPassengerId(Long passengerId);
}
package com.rideshare.repository;

import com.rideshare.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RideRepository extends JpaRepository<Ride, Long> {


    @Query("SELECT r FROM Ride r WHERE LOWER(r.source) = LOWER(:source) AND LOWER(r.destination) = LOWER(:destination) AND DATE(r.dateTime) = :date")
    List<Ride> findRidesBySourceDestinationAndDate(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("date") LocalDate date
    );


    @Query("SELECT r FROM Ride r WHERE DATE(r.dateTime) = :date AND r.availableSeats > 0 AND " +
            "(LOWER(r.source) LIKE %:source% OR LOWER(r.destination) LIKE %:destination%)")
    List<Ride> findPartialRidesBySourceOrDestination(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("date") LocalDate date
    );


    Page<Ride> findByDriverId(Long driverId, Pageable pageable);
}
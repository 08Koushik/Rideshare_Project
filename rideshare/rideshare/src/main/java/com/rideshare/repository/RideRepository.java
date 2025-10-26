package com.rideshare.repository;

import com.rideshare.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {

    // Find rides that match source, destination, and same date (ignoring time)
    @Query("SELECT r FROM Ride r WHERE LOWER(r.source) = LOWER(:source) AND LOWER(r.destination) = LOWER(:destination) AND DATE(r.dateTime) = :date")
    List<Ride> findRidesBySourceDestinationAndDate(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("date") LocalDate date
    );
}

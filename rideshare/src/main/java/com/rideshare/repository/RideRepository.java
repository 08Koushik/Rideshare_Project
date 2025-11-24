package com.rideshare.repository;

import com.rideshare.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {

    // 🔍 Flexible search (partial match, works with LocationIQ long addresses)
    @Query("SELECT r FROM Ride r " +
            "WHERE LOWER(r.source) LIKE LOWER(CONCAT('%', :source, '%')) " +
            "AND LOWER(r.destination) LIKE LOWER(CONCAT('%', :destination, '%')) " +
            "AND DATE(r.dateTime) = :date")
    List<Ride> searchRidesFlexible(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("date") LocalDate date
    );

    // 🔎 Driver ride history
    List<Ride> findByDriverId(Long driverId);
}

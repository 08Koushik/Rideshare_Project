package com.rideshare.controller;

import com.rideshare.entity.Ride;
import com.rideshare.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    @Autowired
    private RideRepository rideRepository;

    // ✅ Post a ride
    @PostMapping("/post")
    public Ride postRide(@RequestBody Ride ride) {
        return rideRepository.save(ride);
    }

    // ✅ Search rides
    @GetMapping("/search")
    public List<Ride> searchRides(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam String date
    ) {
        LocalDate searchDate = LocalDate.parse(date);
        return rideRepository.findRidesBySourceDestinationAndDate(source, destination, searchDate);
    }
}

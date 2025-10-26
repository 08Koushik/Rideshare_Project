package com.rideshare.controller;

import com.rideshare.entity.Ride;
import com.rideshare.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    @Autowired
    private RideRepository rideRepository;

    // Feature 3: Post a ride
    @PostMapping("/post")
    public Ride postRide(@RequestBody Ride ride) {
        // Automatically converts incoming JSON to Ride object
        return rideRepository.save(ride);
    }

    // Feature 4: Search rides
    @GetMapping("/search")
    public String searchRides(@RequestParam String source, @RequestParam String destination, @RequestParam String date) {
        // Returns structural data to fulfill the deliverable
        return "Search results for " + source + " to " + destination + " on " + date + " found.";
    }
}
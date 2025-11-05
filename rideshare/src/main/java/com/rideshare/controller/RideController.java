package com.rideshare.controller;

import com.rideshare.entity.Ride;
import com.rideshare.repository.RideRepository;
import com.rideshare.service.FileStorageService;
import com.rideshare.service.GeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // NEW IMPORT
import java.io.IOException;
import com.rideshare.dto.RideDetailsResponse; // NEW IMPORT
import com.rideshare.service.RideSearchService;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    @Autowired
    private RideRepository rideRepository;

    @Autowired // NEW INJECTION
    private GeoService geoService;

    @Autowired
    private RideSearchService rideSearchService;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping(value = "/post", consumes = {"multipart/form-data"})
    public Ride postRide(
            @RequestPart("rideData") Ride ride, // Changed from @RequestBody
            @RequestPart(value = "vehicleImage", required = false) MultipartFile vehicleImage) throws IOException { // NEW FILE PART

        // 1. Handle Image Upload and set the reference URL
        if (vehicleImage != null && !vehicleImage.isEmpty()) {
            String imagePath = fileStorageService.storeFile(vehicleImage); // Saves file and returns web path
            ride.setVehicleImageReference(imagePath); // Set the accessible URL/path
        }

        double distance = geoService.getDistanceInKm(ride.getSource(), ride.getDestination());
        double totalFare = geoService.calculateFare(distance);
        ride.setFarePerSeat(totalFare / ride.getAvailableSeats());
        return rideRepository.save(ride);
    }

    // ✅ Search rides - UPDATED TO INCLUDE PARTIAL MATCHES (Route Matching)
    @GetMapping("/search")
    public List<RideDetailsResponse> searchRides(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam String date
    ) {
        LocalDate searchDate = LocalDate.parse(date);

        // 1. Get Direct Matches (Exact Source and Destination)
        List<Ride> directMatches = rideRepository.findRidesBySourceDestinationAndDate(source, destination, searchDate);

        // 2. Get Partial Matches (Rides along the route - simple heuristic)
        // NOTE: findPartialRidesBySourceOrDestination method must be added to RideRepository
        List<Ride> partialMatches = rideRepository.findPartialRidesBySourceOrDestination(source, destination, searchDate);

        // 3. Combine and remove duplicates using a Set
        Set<Ride> allRides = new HashSet<>(directMatches);
        allRides.addAll(partialMatches);

        return rideSearchService.searchRides(source, destination, date);
    }

    // NEW ENDPOINT for Driver Ride History
    @GetMapping("/driver/{driverId}/history")
    public List<Ride> getDriverRideHistory(@PathVariable Long driverId) {
        // NOTE: findByDriverId method must be added to RideRepository
        return rideRepository.findByDriverId(driverId);
    }
}
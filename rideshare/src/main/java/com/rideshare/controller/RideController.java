package com.rideshare.controller;

import com.rideshare.entity.Ride;
import com.rideshare.entity.User;
import com.rideshare.repository.RideRepository;
import com.rideshare.repository.UserRepository;
import com.rideshare.service.FileStorageService;
import com.rideshare.service.GeoService;
import com.rideshare.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.rideshare.dto.RideDetailsResponse;
import com.rideshare.service.RideSearchService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeoService geoService;

    @Autowired
    private RideSearchService rideSearchService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private EmailService emailService;

    // -------------------------------------------------------------
    // ✅ POST A RIDE
    // -------------------------------------------------------------
    @PostMapping(value = "/post", consumes = {"multipart/form-data"})
    public Ride postRide(
            @RequestPart("rideData") Ride ride,
            @RequestPart(value = "vehicleImages", required = false) List<MultipartFile> vehicleImages
    ) throws IOException {

        // Save vehicle images
        List<String> imagePaths = new ArrayList<>();

        if (vehicleImages != null && !vehicleImages.isEmpty()) {
            for (MultipartFile vehicleImage : vehicleImages) {
                if (!vehicleImage.isEmpty()) {
                    String path = fileStorageService.storeFile(vehicleImage);
                    imagePaths.add(path);
                }
            }
        }

        ride.setVehicleImageReference(String.join(";", imagePaths));

        // Calculate fare automatically
        double distance = geoService.getDistanceInKm(ride.getSource(), ride.getDestination());
        double totalFare = geoService.calculateFare(distance);
        double farePerSeat = totalFare / ride.getAvailableSeats();

        ride.setFarePerSeat(farePerSeat);

        Ride savedRide = rideRepository.save(ride);

        // Send ride post confirmation email
        User driver = userRepository.findById(ride.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found."));

        emailService.sendRidePostConfirmation(
                driver.getEmail(),
                driver.getName(),
                savedRide.getSource(),
                savedRide.getDestination(),
                savedRide.getDateTime().toString(),
                farePerSeat
        );

        return savedRide;
    }

    // -------------------------------------------------------------
    // ✅ SEARCH RIDES (FIXED – REAL PARTIAL SEARCH)
    // -------------------------------------------------------------
    @GetMapping("/search")
    public List<RideDetailsResponse> searchRides(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam String date
    ) {

        LocalDate searchDate = LocalDate.parse(date.trim());

        // Flexible search → matches ANY part of LocationIQ address
        List<Ride> results = rideRepository.searchRidesFlexible(
                source.trim(),
                destination.trim(),
                searchDate
        );

        // Convert Ride → RideDetailsResponse
        return results.stream()
                .map(rideSearchService::mapToRideDetails)
                .toList();
    }

    // -------------------------------------------------------------
    // ✅ DRIVER RIDE HISTORY
    // -------------------------------------------------------------
    @GetMapping("/driver/{driverId}/history")
    public List<Ride> getDriverRideHistory(@PathVariable Long driverId) {
        return rideRepository.findByDriverId(driverId);
    }

    // -------------------------------------------------------------
    // ✅ ADMIN – GET ALL RIDES
    // -------------------------------------------------------------
    @GetMapping("/admin/rides")
    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }
}

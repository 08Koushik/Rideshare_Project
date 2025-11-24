package com.rideshare.service;

import com.rideshare.dto.RideDetailsResponse;
import com.rideshare.entity.Ride;
import com.rideshare.entity.User;
import com.rideshare.repository.RideRepository;
import com.rideshare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RideSearchService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    // 🔍 NEW FLEXIBLE SEARCH – matches long LocationIQ addresses
    public List<RideDetailsResponse> searchRides(String source, String destination, String date) {

        LocalDate searchDate = LocalDate.parse(date);

        // 1️⃣ Fetch rides using flexible matching
        List<Ride> matchedRides = rideRepository.searchRidesFlexible(
                source.trim(),
                destination.trim(),
                searchDate
        );

        if (matchedRides.isEmpty()) {
            return List.of();
        }

        // 2️⃣ Extract driver IDs
        Set<Long> driverIds = matchedRides.stream()
                .map(Ride::getDriverId)
                .collect(Collectors.toSet());

        // 3️⃣ Fetch driver details (batch lookup)
        List<User> drivers = userRepository.findAllById(driverIds);

        // 4️⃣ Convert to DTO
        return matchedRides.stream().map(ride -> {

            User driver = drivers.stream()
                    .filter(d -> d.getId().equals(ride.getDriverId()))
                    .findFirst()
                    .orElse(null);

            if (driver == null) return null;

            List<String> images = new ArrayList<>();
            if (ride.getVehicleImageReference() != null) {
                images = Arrays.asList(ride.getVehicleImageReference().split(";"));
            }

            return new RideDetailsResponse(ride, driver, images);

        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    // Helper to map a single ride — used by RideController
    public RideDetailsResponse mapToRideDetails(Ride ride) {

        User driver = userRepository.findById(ride.getDriverId()).orElse(null);
        if (driver == null) return null;

        List<String> images = new ArrayList<>();
        if (ride.getVehicleImageReference() != null) {
            images = Arrays.asList(ride.getVehicleImageReference().split(";"));
        }

        return new RideDetailsResponse(ride, driver, images);
    }
}

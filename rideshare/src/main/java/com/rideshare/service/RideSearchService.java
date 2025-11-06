package com.rideshare.service;

import com.rideshare.dto.RideDetailsResponse;
import com.rideshare.entity.Ride;
import com.rideshare.entity.User;
import com.rideshare.repository.RideRepository;
import com.rideshare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class RideSearchService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    public List<RideDetailsResponse> searchRides(String source, String destination, String date) {
        LocalDate searchDate = LocalDate.parse(date);

        // 1. Find all matching Ride entities (using original logic)
        List<Ride> directMatches = rideRepository.findRidesBySourceDestinationAndDate(source, destination, searchDate);
        List<Ride> partialMatches = rideRepository.findPartialRidesBySourceOrDestination(source, destination, searchDate);

        Set<Ride> allRides = new HashSet<>(directMatches);
        allRides.addAll(partialMatches);

        if (allRides.isEmpty()) {
            return List.of();
        }

        // 2. Collect all unique Driver IDs
        Set<Long> driverIds = allRides.stream()
                .map(Ride::getDriverId)
                .collect(Collectors.toSet());

        // 3. Fetch all necessary Driver (User) details in one go (Efficient)
        List<User> drivers = userRepository.findAllById(driverIds);

        // 4. Map the fetched Rides to the new DTO, incorporating driver details
        return allRides.stream().map(ride -> {
            User driver = drivers.stream()
                    .filter(d -> d.getId().equals(ride.getDriverId()))
                    .findFirst()
                    .orElse(null); // Should not happen if data is consistent

            if (driver == null) {
                // Skip or handle corrupted data
                return null;
            }
            List<String> imageReferences = new ArrayList<>();
            String refString = ride.getVehicleImageReference();
            if (refString != null && !refString.isEmpty()) {
                imageReferences = Arrays.asList(refString.split(";"));
            }

            return new RideDetailsResponse(ride, driver,imageReferences);
        }).filter(r -> r != null).collect(Collectors.toList());
    }
}
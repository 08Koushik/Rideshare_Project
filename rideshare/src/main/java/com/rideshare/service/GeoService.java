package com.rideshare.service;

import org.springframework.stereotype.Service;

@Service
public class GeoService {

    private static final double BASE_FARE = 50.0; // Base rate
    private static final double RATE_PER_KM = 8.5;  // Rate per KM (e.g., ₹8.5)

    // Constants for Haversine Calculation
    private static final double EARTH_RADIUS_KM = 6371;
    private static final String LOCATIONIQ_API_KEY = "pk.8c6dc49f1003f5c138f99ed064ad5a43";


    private double getPseudoCoordinate(String location, boolean isLatitude) {
        // Use a deterministic hash to create unique coordinates for simulation
        int hash = location.hashCode();
        double base = isLatitude ? 17.0 : 78.0;
        double offset = (hash % 1000) / 1000.0;
        return base + offset;
    }

    /**
     * Calculates the distance in Km using the Haversine formula based on simulated coordinates.
     */
    public double getDistanceInKm(String origin, String destination) {
        System.out.println("Using LocationIQ key context (API Call Simulated): " + LOCATIONIQ_API_KEY);

        // 1. SIMULATE GEOCODING
        double lat1 = getPseudoCoordinate(origin, true);
        double lon1 = getPseudoCoordinate(origin, false);
        double lat2 = getPseudoCoordinate(destination, true);
        double lon2 = getPseudoCoordinate(destination, false);

        // 2. HAVERSINE DISTANCE CALCULATION
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS_KM * c;

        // Enforce a practical minimum/maximum for the simulation
        distance = Math.min(100.0, Math.max(10.0, distance));

        System.out.println(String.format("Simulated Haversine Distance: %.2f km", distance));

        return distance;
    }

    public double calculateFare(double distanceInKm) {
        // Fare = Base Fare + (Rate per Km * Distance)
        return BASE_FARE + (RATE_PER_KM * distanceInKm);
    }
}
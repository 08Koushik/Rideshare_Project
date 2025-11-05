package com.rideshare.service;

import org.springframework.stereotype.Service;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class GeoService {

    private static final double BASE_FARE = 50.0; // Base rate (₹50.0)
    private static final double RATE_PER_KM = 8.5;  // Rate per KM (₹8.5)
    private static final double EARTH_RADIUS_KM = 6371;

    private static final String LOCATIONIQ_API_KEY = "pk.8c6dc49f1003f5c138f99ed064ad5a43";

    // Internal class to hold coordinates
    private static class Coordinates {
        double latitude;
        double longitude;
        Coordinates(double lat, double lon) {
            this.latitude = lat;
            this.longitude = lon;
        }
    }

    /**
     * Makes an external API call to LocationIQ to geocode a location string into coordinates.
     */
    private Coordinates fetchCoordinates(String location) {
        String encodedLocation = java.net.URLEncoder.encode(location, java.nio.charset.StandardCharsets.UTF_8);
        String urlString = String.format(
                "https://us1.locationiq.com/v1/search?key=%s&q=%s&format=json&limit=1",
                LOCATIONIQ_API_KEY, encodedLocation);

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                System.err.println("LocationIQ Geocoding failed with response code: " + conn.getResponseCode());
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder jsonResponse = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                jsonResponse.append(line);
            }
            br.close();
            conn.disconnect();

            // Parse the JSON response
            JSONArray results = new JSONArray(jsonResponse.toString());
            if (results.length() > 0) {
                JSONObject firstResult = results.getJSONObject(0);
                double lat = firstResult.getDouble("lat");
                double lon = firstResult.getDouble("lon");
                return new Coordinates(lat, lon);
            }
        } catch (Exception e) {
            System.err.println("Error fetching coordinates for " + location + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Calculates the distance in Km using the Haversine formula based on coordinates fetched from LocationIQ.
     */
    public double getDistanceInKm(String origin, String destination) {
        // 1. Get Coordinates for Source
        Coordinates coord1 = fetchCoordinates(origin);
        // 2. Get Coordinates for Destination
        Coordinates coord2 = fetchCoordinates(destination);

        if (coord1 == null || coord2 == null) {
            // If API fails, use a fallback distance (e.g., 500 km) to prevent extremely cheap fares.
            System.err.println("Could not geocode one or both locations. Returning default distance of 500.0 km.");
            return 500.0;
        }

        // 3. HAVERSINE DISTANCE CALCULATION (Real Haversine using geocoded coordinates)
        double lat1 = coord1.latitude;
        double lon1 = coord1.longitude;
        double lat2 = coord2.latitude;
        double lon2 = coord2.longitude;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS_KM * c;

        // Ensure a minimum distance (e.g., 5 km for a local trip)
        distance = Math.max(5.0, distance);

        System.out.println(String.format("Calculated Haversine Distance: %.2f km", distance));

        // Note: Haversine distance is "as the crow flies," which is the straight line distance.
        // For road distance, you would ideally use the LocationIQ Routing API, but this Haversine implementation
        // using real coordinates is a significant step toward accuracy for the fare calculation.
        return distance;
    }

    public double calculateFare(double distanceInKm) {
        // Fare = Base Fare + (Rate per Km * Distance)
        return BASE_FARE + (RATE_PER_KM * distanceInKm);
    }
}
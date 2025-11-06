package com.rideshare.dto;

import com.rideshare.entity.Ride;
import com.rideshare.entity.User;
import java.time.LocalDateTime;
import java.util.List;

public class RideDetailsResponse {
    private Long rideId;
    private String source;
    private String destination;
    private LocalDateTime dateTime;
    private int availableSeats;
    private Double farePerSeat;

    // Driver Details (from User Entity)
    private String driverName;
    private String driverContact;
    private String driverLicenseNumber;

    // Vehicle Details (from Ride Entity or User Entity)
    private String vehicleName; // From Ride Entity
    private String vehicleType; // From Ride Entity
    private String vehicleNumber; // From User Entity (vehicleDetails)
    private List<String> vehicleImageReferences; // From Ride Entity

    // Constructor to map data from Ride and User entities
    public RideDetailsResponse(Ride ride, User driver,List<String> imageRefs) {
        this.rideId = ride.getId();
        this.source = ride.getSource();
        this.destination = ride.getDestination();
        this.dateTime = ride.getDateTime();
        this.availableSeats = ride.getAvailableSeats();
        this.farePerSeat = ride.getFarePerSeat();

        // Map Driver Details
        this.driverName = driver.getName();
        this.driverContact = driver.getContactNumber();
        this.driverLicenseNumber = driver.getDriverLicenseNumber();
        this.vehicleNumber = driver.getVehicleDetails(); // Vehicle number is stored here

        // Map Vehicle Details (from Ride entity fields added previously)
        this.vehicleName = ride.getVehicleName();
        this.vehicleType = ride.getVehicleType();
        this.vehicleImageReferences = imageRefs;
    }

    // --- Getters and Setters ---

    public Long getRideId() { return rideId; }
    public void setRideId(Long rideId) { this.rideId = rideId; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public Double getFarePerSeat() { return farePerSeat; }
    public void setFarePerSeat(Double farePerSeat) { this.farePerSeat = farePerSeat; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverContact() { return driverContact; }
    public void setDriverContact(String driverContact) { this.driverContact = driverContact; }

    public String getDriverLicenseNumber() { return driverLicenseNumber; }
    public void setDriverLicenseNumber(String driverLicenseNumber) { this.driverLicenseNumber = driverLicenseNumber; }

    public String getVehicleName() { return vehicleName; }
    public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public List<String> getVehicleImageReferences() { return vehicleImageReferences; }
    public void setVehicleImageReferences(List<String> vehicleImageReferences) { this.vehicleImageReferences = vehicleImageReferences; }


}
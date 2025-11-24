package com.rideshare.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "rides")
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source;
    private String destination;
    private LocalDateTime dateTime;
    private int availableSeats;
    private Long driverId;
    private Double farePerSeat;
    private Double sourceLatitude;
    private Double sourceLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;
    private String pickupLocations;
    private String dropLocations;
    private String vehicleName;
    private String vehicleType;
    private String vehicleImageReference;

    private Boolean isAc; // NEW FIELD: AC status

    // ADDED: equals() and hashCode() based on ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ride ride = (Ride) o;
        // Only compare by the unique database ID
        return Objects.equals(id, ride.id);
    }

    @Override
    public int hashCode() {
        // Only hash by the unique database ID
        return Objects.hash(id);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }
    public Double getFarePerSeat() { return farePerSeat; }
    public void setFarePerSeat(Double farePerSeat) { this.farePerSeat = farePerSeat; }
    public Double getSourceLatitude() { return sourceLatitude; }
    public void setSourceLatitude(Double sourceLatitude) { this.sourceLatitude = sourceLatitude; }
    public Double getSourceLongitude() { return sourceLongitude; }
    public void setSourceLongitude(Double sourceLongitude) { this.sourceLongitude = sourceLongitude; }
    public Double getDestinationLatitude() { return destinationLatitude; }
    public void setDestinationLatitude(Double destinationLatitude) { this.destinationLatitude = destinationLatitude; }
    public Double getDestinationLongitude() { return destinationLongitude; }
    public void setDestinationLongitude(Double destinationLongitude) { this.destinationLongitude = destinationLongitude; }
    public String getPickupLocations() { return pickupLocations; }
    public void setPickupLocations(String pickupLocations) { this.pickupLocations = pickupLocations; }
    public String getDropLocations() { return dropLocations; }
    public void setDropLocations(String dropLocations) { this.dropLocations = dropLocations; }
    public String getVehicleName() { return vehicleName; }
    public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public String getVehicleImageReference() { return vehicleImageReference; }
    public void setVehicleImageReference(String vehicleImageReference) { this.vehicleImageReference = vehicleImageReference; }

    // NEW GETTERS/SETTERS
    public Boolean getIsAc() { return isAc; }
    public void setIsAc(Boolean isAc) { this.isAc = isAc; }
}
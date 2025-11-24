package com.rideshare.dto;

import java.time.LocalDateTime;

public class PassengerBookingDTO {
    private Long bookingId;
    private Long rideId;
    private String status;
    private int seatsBooked;
    private String source;
    private String destination;
    private LocalDateTime dateTime;
    private String driverName;
    private String driverContact;
    private String vehicleNumber;
    private double farePerSeat;
    private double totalFare;

    // Constructors
    public PassengerBookingDTO() {}

    public PassengerBookingDTO(Long bookingId, Long rideId, String status, int seatsBooked,
                              String source, String destination, LocalDateTime dateTime,
                              String driverName, String driverContact, String vehicleNumber,
                              double farePerSeat, double totalFare) {
        this.bookingId = bookingId;
        this.rideId = rideId;
        this.status = status;
        this.seatsBooked = seatsBooked;
        this.source = source;
        this.destination = destination;
        this.dateTime = dateTime;
        this.driverName = driverName;
        this.driverContact = driverContact;
        this.vehicleNumber = vehicleNumber;
        this.farePerSeat = farePerSeat;
        this.totalFare = totalFare;
    }

    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSeatsBooked() {
        return seatsBooked;
    }

    public void setSeatsBooked(int seatsBooked) {
        this.seatsBooked = seatsBooked;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverContact() {
        return driverContact;
    }

    public void setDriverContact(String driverContact) {
        this.driverContact = driverContact;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public double getFarePerSeat() {
        return farePerSeat;
    }

    public void setFarePerSeat(double farePerSeat) {
        this.farePerSeat = farePerSeat;
    }

    public double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(double totalFare) {
        this.totalFare = totalFare;
    }
}

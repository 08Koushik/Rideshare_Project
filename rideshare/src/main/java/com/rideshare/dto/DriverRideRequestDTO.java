package com.rideshare.dto;

public class DriverRideRequestDTO {
    private Long bookingId;
    private Long rideId;
    private String passengerName;
    private String passengerContact;
    private String source;
    private String destination;
    private int seats;
    private String status;

    // Constructor to easily create the DTO from service data
    public DriverRideRequestDTO(Long bookingId, Long rideId, String passengerName, String passengerContact, String source, String destination, int seats, String status) {
        this.bookingId = bookingId;
        this.rideId = rideId;
        this.passengerName = passengerName;
        this.passengerContact = passengerContact;
        this.source = source;
        this.destination = destination;
        this.seats = seats;
        this.status = status;
    }

    // --- Getters and Setters (Required for JSON serialization/deserialization) ---

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

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerContact() {
        return passengerContact;
    }

    public void setPassengerContact(String passengerContact) {
        this.passengerContact = passengerContact;
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

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
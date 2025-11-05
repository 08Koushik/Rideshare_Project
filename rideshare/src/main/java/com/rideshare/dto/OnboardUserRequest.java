package com.rideshare.dto;

// This DTO mirrors the fields you need from the front-end form
public class OnboardUserRequest {

    private String name;
    private String email;
    private String contact;
    private String role;
    private String vehicle; // For Vehicle Number
    private String driverLicenseNumber;
    private String aadharNumber;

    // --- Getters and Setters (REQUIRED for Spring) ---

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getVehicle() { return vehicle; }
    public void setVehicle(String vehicle) { this.vehicle = vehicle; }

    public String getDriverLicenseNumber() { return driverLicenseNumber; }
    public void setDriverLicenseNumber(String driverLicenseNumber) { this.driverLicenseNumber = driverLicenseNumber; }

    public String getAadharNumber() { return aadharNumber; }
    public void setAadharNumber(String aadharNumber) { this.aadharNumber = aadharNumber; }
}
package com.rideshare.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String contactNumber;
    private String password;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    private boolean isFirstLogin;
    private String vehicleDetails;

    private String driverLicenseNumber;
    private String aadharNumber;

    // NEW FIELDS FOR ADMIN STATUS MANAGEMENT
    private boolean verified = false;  // Default is false
    private boolean blocked = false;   // Default is false

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public RoleType getRoleType() { return roleType; }
    public void setRoleType(RoleType roleType) { this.roleType = roleType; }

    public boolean isFirstLogin() { return isFirstLogin; }
    public void setFirstLogin(boolean firstLogin) { isFirstLogin = firstLogin; }

    public String getVehicleDetails() { return vehicleDetails; }
    public void setVehicleDetails(String vehicleDetails) { this.vehicleDetails = vehicleDetails; }

    public String getDriverLicenseNumber() { return driverLicenseNumber; }
    public void setDriverLicenseNumber(String driverLicenseNumber) { this.driverLicenseNumber = driverLicenseNumber; }

    public String getAadharNumber() { return aadharNumber; }
    public void setAadharNumber(String aadharNumber) { this.aadharNumber = aadharNumber; }

    // NEW GETTERS & SETTERS
    public boolean isVerified() { return verified; } //
    public void setVerified(boolean verified) { this.verified = verified; } //

    public boolean isBlocked() { return blocked; } //
    public void setBlocked(boolean blocked) { this.blocked = blocked; } //
}
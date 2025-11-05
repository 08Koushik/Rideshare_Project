package com.rideshare.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "driver_wallets")
public class DriverWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long driverId; // Links to the User table (Driver)

    private Double balance = 0.0; // Current balance available for withdrawal
    private Double totalEarnings = 0.0; // Total money earned lifetime
    private Double totalCommission = 0.0; // Total commission deducted lifetime

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }
    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
    public Double getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(Double totalEarnings) { this.totalEarnings = totalEarnings; }
    public Double getTotalCommission() { return totalCommission; }
    public void setTotalCommission(Double totalCommission) { this.totalCommission = totalCommission; }
}
package com.rideshare.dto;

public class AdminReportDTO {
    private long totalRides;
    private double totalEarnings;
    private long totalCancellations;
    private long totalBookings;
    private long totalPayments;

    public long getTotalRides() { return totalRides; }
    public void setTotalRides(long totalRides) { this.totalRides = totalRides; }

    public double getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(double totalEarnings) { this.totalEarnings = totalEarnings; }

    public long getTotalCancellations() { return totalCancellations; }
    public void setTotalCancellations(long totalCancellations) { this.totalCancellations = totalCancellations; }

    public long getTotalBookings() { return totalBookings; }
    public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }

    public long getTotalPayments() { return totalPayments; }
    public void setTotalPayments(long totalPayments) { this.totalPayments = totalPayments; }
}

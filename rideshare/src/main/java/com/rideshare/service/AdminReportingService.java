package com.rideshare.service;

import com.rideshare.dto.AdminReportDTO;
import com.rideshare.entity.Payment;
import com.rideshare.entity.PaymentStatus;
import com.rideshare.repository.BookingRepository;
import com.rideshare.repository.PaymentRepository;
import com.rideshare.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminReportingService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public AdminReportDTO getSystemReport() {
        AdminReportDTO report = new AdminReportDTO();

        report.setTotalRides(rideRepository.count());
        report.setTotalBookings(bookingRepository.count());

        List<Payment> allPayments = paymentRepository.findAll();
        report.setTotalPayments(allPayments.size());

        report.setTotalEarnings(allPayments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .mapToDouble(Payment::getAmount)
                .sum());

        long cancellations = bookingRepository.findAll().stream()
                .filter(b -> {
                    String s = b.getStatus();
                    return s != null && (s.equalsIgnoreCase("DENIED") || s.equalsIgnoreCase("CANCELED"));
                })
                .count();
        report.setTotalCancellations(cancellations);

        return report;
    }
}

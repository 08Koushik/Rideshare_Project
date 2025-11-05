package com.rideshare.repository;

import com.rideshare.entity.DriverWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DriverWalletRepository extends JpaRepository<DriverWallet, Long> {
    // Method to find the wallet linked to a specific driverId
    Optional<DriverWallet> findByDriverId(Long driverId);
}
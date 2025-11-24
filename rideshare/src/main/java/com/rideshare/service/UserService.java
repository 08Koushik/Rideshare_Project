package com.rideshare.service;

import com.rideshare.entity.RoleType;
import com.rideshare.entity.User;
import com.rideshare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService; // assumes you already have this bean

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Hard-coded admin credentials
    private final String ADMIN_EMAIL = "admin@rideshare.com";
    private final String ADMIN_PASS  = "admin@123";

    public boolean validateAdmin(String email, String password) {
        return ADMIN_EMAIL.equals(email) && ADMIN_PASS.equals(password);
    }

    public User onboardUser(
            String name,
            String email,
            String contact,
            String role,
            String vehicle,
            String driverLicenseNumber,
            String aadharNumber
    ) {
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("Email is required!");
        }
        if (contact == null || contact.isBlank()) {
            throw new IllegalStateException("Contact number is required!");
        }
        if ("DRIVER".equalsIgnoreCase(role) && (vehicle == null || vehicle.isBlank())) {
            throw new IllegalStateException("Vehicle number is required for drivers!");
        }

        // Pre-check duplicate email to return clean 409 to frontend
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("EMAIL_EXISTS");
        }

        String tempPassword = "temp" + new Random().nextInt(10000);

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setContactNumber(contact);
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setFirstLogin(true);

        // Map role -> RoleType
        RoleType roleType;
        if ("ADMIN".equalsIgnoreCase(role)) roleType = RoleType.ADMIN;
        else if ("DRIVER".equalsIgnoreCase(role)) roleType = RoleType.DRIVER;
        else roleType = RoleType.PASSENGER;

        user.setRoleType(roleType);

        if (roleType == RoleType.DRIVER) {
            user.setVehicleDetails(vehicle);
            user.setDriverLicenseNumber(driverLicenseNumber);
        } else if (roleType == RoleType.PASSENGER) {
            user.setAadharNumber(aadharNumber);
        }

        User saved = userRepository.save(user);

        // Send temp password email (best-effort)
        try {
            emailService.sendTemporaryPassword(email, name, tempPassword);
        } catch (Exception e) {
            System.err.println("Email sending failed for " + email + ": " + e.getMessage());
        }

        return saved;
    }

    public Object userLogin(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Incorrect password!");
        }

        if (user.isFirstLogin()) {
            throw new RuntimeException("FIRST_LOGIN");
        }

        // Return User object (frontend parses and redirects based on roleType)
        return user;
    }

    public String resetPassword(String email, String newPassword) {
        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return "User not found!";

        User user = opt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setFirstLogin(false);
        userRepository.save(user);
        return "Password updated successfully!";
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteAllUsers() {
        List<User> users = userRepository.findAll();
        users.removeIf(u -> u.getRoleType() == RoleType.ADMIN);
        userRepository.deleteAll(users);
    }
}

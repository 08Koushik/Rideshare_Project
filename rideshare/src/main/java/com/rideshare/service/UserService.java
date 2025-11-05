package com.rideshare.service;

import com.rideshare.entity.RoleType;
import com.rideshare.entity.User;
import com.rideshare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // NEW IMPORT

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // NEW INJECTION: USED FOR LOGIN/RESET

    // Default Admin credentials
    private final String ADMIN_EMAIL = "admin@rideshare.com";
    private final String ADMIN_PASS = "admin@123";

    // Validate Admin login
    public boolean validateAdmin(String email, String password) {
        return ADMIN_EMAIL.equals(email) && ADMIN_PASS.equals(password);
    }

    // Onboard Driver or Passenger
    public User onboardUser(
            String name,
            String email,
            String contact,
            String role,
            String vehicle, // Vehicle Number
            String driverLicenseNumber, // NEW
            String aadharNumber // NEW
    ) {
        String tempPassword = "temp" + new Random().nextInt(10000);
        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setContactNumber(contact);

        // CRITICAL FIX: HASH THE PASSWORD BEFORE SAVING
        user.setPassword(passwordEncoder.encode(tempPassword));

        user.setFirstLogin(true);

        // Set roleType
        if (role.equalsIgnoreCase("ADMIN")) user.setRoleType(RoleType.ADMIN);
        else if (role.equalsIgnoreCase("DRIVER")) user.setRoleType(RoleType.DRIVER);
        else user.setRoleType(RoleType.PASSENGER);

        // Set role-specific details
        if (user.getRoleType() == RoleType.DRIVER) {
            user.setVehicleDetails(vehicle);
            user.setDriverLicenseNumber(driverLicenseNumber);
        } else if (user.getRoleType() == RoleType.PASSENGER) {
            user.setAadharNumber(aadharNumber);
        }

        User savedUser = userRepository.save(user);

        // Email logic (uses the clear-text tempPassword for the email body)
        try {
            emailService.sendTemporaryPassword(email, name, tempPassword);
            System.out.println("Temporary password email sent to: " + email);
        } catch (Exception e) {
            System.err.println("Email sending failed for user: " + email + ". Error: " + e.getMessage());
        }

        return savedUser;
    }


    // User login
    public User userLogin(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        User user = userOpt.get();

        // CRITICAL FIX: USE BCryptPasswordEncoder.matches() for validation
        if (!passwordEncoder.matches(password.trim(), user.getPassword().trim())) {
            throw new RuntimeException("Incorrect password!");
        }
        // --------------------------------------------------------

        // 3. First Login -> throw special exception
        if (user.isFirstLogin()) {
            throw new RuntimeException("FIRST_LOGIN");
        }

        return user;
    }

    // Reset password
    public String resetPassword(String email, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return "User not found!";

        User user = userOpt.get();

        // FIX: HASH the new password before saving
        user.setPassword(passwordEncoder.encode(newPassword));

        user.setFirstLogin(false);
        userRepository.save(user);
        return "Password updated successfully!";
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // FIX: Correct deletion logic to preserve Admin user
    public void deleteAllUsers() {
        List<User> usersToDelete = userRepository.findAll();

        // Filter out users with the ADMIN role before deleting
        usersToDelete.removeIf(user -> user.getRoleType() == RoleType.ADMIN);

        // Delete ONLY the filtered list of non-admin users
        userRepository.deleteAll(usersToDelete);
    }
}
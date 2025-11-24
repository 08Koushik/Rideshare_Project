package com.rideshare.controller;

import com.rideshare.dto.OnboardUserRequest;
import com.rideshare.entity.User;
import com.rideshare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.rideshare.dto.AdminReportDTO;
import com.rideshare.service.AdminReportingService;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminReportingService adminReportingService;

    // Admin login
    @PostMapping("/admin/login")
    public String adminLogin(@RequestParam String email, @RequestParam String password) {
        if (userService.validateAdmin(email, password))
            return "Admin logged in successfully!";
        else
            return "Invalid admin credentials!";
    }

    // Onboard user
    @PostMapping("/admin/onboard")
    public User onboardUser(
            @RequestBody OnboardUserRequest request
    ) {

        return userService.onboardUser(
                request.getName(),
                request.getEmail(),
                request.getContact(),
                request.getRole(),
                request.getVehicle(),
                request.getDriverLicenseNumber(),
                request.getAadharNumber()
        );
    }
    @PostMapping("/register")
    public User registerUser(@RequestBody OnboardUserRequest request) {
        String role = request.getRole();
        if (role == null || role.equalsIgnoreCase("ADMIN")) {
            role = "PASSENGER";
        }

        return userService.onboardUser(
                request.getName(),
                request.getEmail(),
                request.getContact(),
                role,
                request.getVehicle(),
                request.getDriverLicenseNumber(),
                request.getAadharNumber()
        );
    }


    @PostMapping("/login")

    public Object userLogin(@RequestParam String email, @RequestParam String password) {
        try {

            User user = userService.userLogin(email, password);

            return user;
        } catch (RuntimeException e) {

            return e.getMessage();
        }
    }

    // Reset password
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        return userService.resetPassword(email, newPassword);
    }

    @GetMapping("/admin/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // NEW ENDPOINT: Update User Status (Admin function)
    @PostMapping("/admin/user/{userId}/status")
    public User updateStatus(
            @PathVariable Long userId,
            @RequestParam String type,
            @RequestParam boolean value
    ) {
        return userService.updateUserStatus(userId, type, value);
    }

    @DeleteMapping("/delete-all-users")
    public ResponseEntity<String> deleteAllUsers() {
        userService.deleteAllUsers();
        return ResponseEntity.ok("All users deleted successfully!");
    }
    @GetMapping("/admin/report")
    public AdminReportDTO getReport() {
        return adminReportingService.getSystemReport();
    }
}
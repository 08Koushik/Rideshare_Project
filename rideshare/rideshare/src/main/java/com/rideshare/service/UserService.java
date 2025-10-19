package com.rideshare.service;

import com.rideshare.entity.RoleType;
import com.rideshare.entity.User;
import com.rideshare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Default Admin credentials
    private final String ADMIN_EMAIL = "admin@rideshare.com";
    private final String ADMIN_PASS = "admin@123";

    // Validate Admin login
    public boolean validateAdmin(String email, String password) {
        return ADMIN_EMAIL.equals(email) && ADMIN_PASS.equals(password);
    }

    // Onboard Driver or Passenger
    public User onboardUser(String name, String email, String contact, String role, String vehicle) {
        String tempPassword = "temp" + new Random().nextInt(10000); // generate temporary password
        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setContactNumber(contact);
        user.setPassword(tempPassword);
        user.setFirstLogin(true);

        // Set roleType
        if (role.equalsIgnoreCase("ADMIN")) user.setRoleType(RoleType.ADMIN);
        else if (role.equalsIgnoreCase("DRIVER")) user.setRoleType(RoleType.DRIVER);
        else user.setRoleType(RoleType.PASSENGER);

        // Set vehicle only for drivers
        if (role.equalsIgnoreCase("DRIVER")) user.setVehicleDetails(vehicle);

        return userRepository.save(user);
    }

    // In com.rideshare.service.UserService.java

    // User login
    // In com.rideshare.service.UserService.java

    // Change return type from String to User
    public User userLogin(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        // 1. User not found -> throw exception
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        User user = userOpt.get();

        // 2. Password mismatch -> throw exception (using .trim() for robustness)
        if (!user.getPassword().trim().equals(password.trim())) {
            throw new RuntimeException("Incorrect password!");
        }

        // 3. First Login -> throw special exception
        if (user.isFirstLogin()) {
            throw new RuntimeException("FIRST_LOGIN"); // Signals forced password reset
        }

        // 4. Successful final login -> return the User object
        return user;
    }

    // Reset password
    public String resetPassword(String email, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return "User not found!";

        User user = userOpt.get();
        user.setPassword(newPassword);
        user.setFirstLogin(false);
        userRepository.save(user);
        return "Password updated successfully!";
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public void deleteAllUsers() {
        // Optional: if you want to keep admin in DB
        userRepository.deleteAll(); // this deletes everything
    }


}

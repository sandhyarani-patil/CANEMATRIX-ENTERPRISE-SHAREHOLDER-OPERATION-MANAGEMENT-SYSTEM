package com.example.farmer.canematrix.config;

import com.example.farmer.canematrix.entity.User;
import com.example.farmer.canematrix.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Jar database madhe 'admin' navacha user nasel, tar to automatic tayar hoil
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin@123")); // Default password
            admin.setRole("ROLE_ADMIN");

            userRepository.save(admin);
            System.out.println("👉 Default Admin Created Successfully: username -> admin, password -> admin123");
        }
    }
}
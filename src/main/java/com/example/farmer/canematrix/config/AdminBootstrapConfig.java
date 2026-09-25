package com.example.farmer.canematrix.config;

import com.example.farmer.canematrix.entity.User;
import com.example.farmer.canematrix.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrapConfig {

    @Bean
    public CommandLineRunner createDefaultAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123")); // Default secure password
                admin.setRole("ROLE_ADMIN");
                userRepository.save(admin);
                System.out.println(">> Default ADMIN user (username: admin, password: admin123) created successfully!");
            }
        };
    }
}
package com.example.farmer.canematrix.security;

import com.example.farmer.canematrix.entity.User;
import com.example.farmer.canematrix.entity.Farmer;
import com.example.farmer.canematrix.repository.UserRepository;
import com.example.farmer.canematrix.repository.FarmerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FarmerRepository farmerRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // 1. Pehla User (Admin/Staff) table check karu
        Optional<User> userOpt = userRepository.findByUsername(identifier);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole().replace("ROLE_", ""))
                    .build();
        }

        // 2. Farmer table madhe pehla 'farmerCode' varun check karu
        Optional<Farmer> farmerOpt = farmerRepository.findByFarmerCode(identifier);

        // 3. Jar farmerCode varun nasel sapadla, tar 'mobileNumber' varun check karu
        if (!farmerOpt.isPresent()) {
            farmerOpt = farmerRepository.findByMobileNumber(identifier);
        }

        if (farmerOpt.isPresent()) {
            Farmer farmer = farmerOpt.get();
            // Ithe check kara tumchya Farmer entity madhe getFarmerCode() ahe ki getMobileNumber()
            return org.springframework.security.core.userdetails.User.builder()
                    .username(farmer.getFarmerCode()) // Kinva farmer.getMobileNumber()
                    .password(farmer.getPassword())
                    .roles("FARMER")
                    .build();
        }

        throw new UsernameNotFoundException("User or Farmer not found with identifier: " + identifier);
    }
}
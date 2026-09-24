package com.example.farmer.canematrix.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF डिसेबल (REST API साठी)
                .authorizeHttpRequests(auth -> auth
                        // लॉगिन आणि पब्लिक URL विनासिक्युरिटी उघडे ठेवणे
                        .requestMatchers("/api/auth/**").permitAll()
                        // बाकी सर्व API साठी ऑथेंटिकेशन किंवा रोल गरजेचा असणे
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT वापरल्यामुळे Session Stateless असेल
                );

        return http.build();
    }
}
package com.example.farmer.canematrix.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer; // 👈 हे इम्पोर्ट करा
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // 👈 CORS इथे सुरू करणे अनिवार्य आहे
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 브라우झरच्या Preflight (OPTIONS) रिक्वेस्ट्सना विनाअट परवानगी द्या
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // तुमच्या लॉगिन आणि पब्लिक युआरएल्स
                        .requestMatchers("/api/auth/login", "/api/auth/farmer-login", "/api/public/**").permitAll()

                        // फामर्स किंवा इतर पब्लिक डेटासाठी (गरजेनुसार पर्मिट किंवा ऑथेंटिकेट)
                        .requestMatchers("/api/farmers/**").permitAll() // टेस्टिंगसाठी तात्पुरते पर्मिट करून पाहू शकता

                        .requestMatchers(HttpMethod.POST, "/api/auth/register").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
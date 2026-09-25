package com.example.farmer.canematrix.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "factory_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // उदा. admin किंवा clerk1

    @Column(nullable = false)
    private String password; // BCrypt ने एन्कोड केलेला पासवर्ड

    @Column(nullable = false)
    private String role; // "ROLE_ADMIN" किंवा "ROLE_CLERK"
}
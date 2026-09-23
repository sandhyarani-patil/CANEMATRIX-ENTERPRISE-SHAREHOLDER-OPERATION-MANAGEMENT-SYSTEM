package com.example.farmer.canematrix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "farmer_nominees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmerNominee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "farmer_id", nullable = false, unique = true)
    private Farmer farmer;

    @NotBlank(message = "Nominee name is required")
    @Column(name = "nominee_name", nullable = false, length = 100)
    private String nomineeName;

    @NotBlank(message = "Relation with farmer is required")
    @Column(name = "relation", nullable = false, length = 50)
    private String relation;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
    @Column(name = "mobile_number", length = 10)
    private String mobileNumber;
}
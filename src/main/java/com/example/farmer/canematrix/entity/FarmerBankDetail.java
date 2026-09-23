package com.example.farmer.canematrix.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "farmer_bank_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmerBankDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "farmer_id", nullable = false, unique = true)
    private Farmer farmer;

    @NotBlank(message = "Bank name is required")
    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

    @NotBlank(message = "Branch name is required")
    @Column(name = "branch_name", nullable = false, length = 100)
    private String branchName;

    @NotBlank(message = "Account number is required")
    @Column(name = "account_number", nullable = false, length = 30)
    private String accountNumber;

    @NotBlank(message = "Account holder name is required")
    @Column(name = "account_holder_name", nullable = false, length = 100)
    private String accountHolderName;

    @NotBlank(message = "IFSC code is required")
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC code format")
    @Column(name = "ifsc_code", nullable = false, length = 11)
    private String ifscCode;

    @Column(name = "is_primary")
    private Boolean isPrimary = true;
}
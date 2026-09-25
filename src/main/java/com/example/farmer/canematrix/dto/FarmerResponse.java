package com.example.farmer.canematrix.dto;

import com.example.farmer.canematrix.dto.BankDetailResponse;
import com.example.farmer.canematrix.dto.FarmDetailResponse;
import com.example.farmer.canematrix.dto.NomineeResponse;
import com.example.farmer.canematrix.entity.FarmerStatus;
import com.example.farmer.canematrix.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmerResponse {

    private Long id;
    private String farmerCode;
    private String farmerName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String mobileNumber;
    private String alternateMobileNumber;
    private String email;
    private String address;
    private String village;
    private String taluka;
    private String district;
    private String state;
    private String pincode;
    private String panNumber;
    private String aadharNumber;
    private LocalDate registrationDate;
    private FarmerStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- नवीन जोडलेले Fields ---
    private Boolean has712;
    private Boolean has8A;
    private Double farmArea;

    // --- Nested Objects ---
    private BankDetailResponse bankDetail;
    private NomineeResponse nominee;
    private List<FarmDetailResponse> farmDetails;
}
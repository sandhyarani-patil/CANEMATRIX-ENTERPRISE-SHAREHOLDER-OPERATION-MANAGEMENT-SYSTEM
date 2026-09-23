package com.example.farmer.canematrix.dto.request;

import com.example.farmer.canematrix.dto.BankDetailRequest;
import com.example.farmer.canematrix.dto.FarmDetailRequest;
import com.example.farmer.canematrix.dto.NomineeRequest;
import com.example.farmer.canematrix.entity.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmerRequest {

    private String farmerCode;

    @NotBlank(message = "Farmer name is required")
    private String farmerName;

    private Gender gender;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
    private String mobileNumber;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid alternate mobile number")
    private String alternateMobileNumber;

    @Email(message = "Invalid email address")
    private String email;

    private String address;
    private String village;
    private String taluka;
    private String district;
    private String state;

    @Pattern(regexp = "^\\d{6}$", message = "Pincode must be 6 digits")
    private String pincode;

    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN number format")
    private String panNumber;

    @Pattern(regexp = "^\\d{12}$", message = "Aadhar number must be 12 digits")
    private String aadharNumber;

    private LocalDate registrationDate;

    // --- नवीन जोडलेले Fields ---
    private Boolean has712;
    private Boolean has8A;
    private Double farmArea;

    // --- Nested Objects ---
    @Valid
    private BankDetailRequest bankDetail;

    @Valid
    private NomineeRequest nominee;

    @Valid
    private List<FarmDetailRequest> farmDetails;
}
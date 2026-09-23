package com.example.farmer.canematrix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "farmers",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_farmer_code", columnNames = "farmer_code"),
                @UniqueConstraint(name = "uk_farmer_mobile", columnNames = "mobile_number")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Farmer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "farmer_code", nullable = false, unique = true, length = 20)
    private String farmerCode;

    @NotBlank(message = "Farmer name is required")
    @Column(name = "farmer_name", nullable = false, length = 100)
    private String farmerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20)
    private Gender gender;

    @Past(message = "Date of birth must be in the past")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
    @Column(name = "mobile_number", nullable = false, unique = true, length = 10)
    private String mobileNumber;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid alternate mobile number")
    @Column(name = "alternate_mobile_number", length = 10)
    private String alternateMobileNumber;

    @Email(message = "Invalid email address")
    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "village", length = 100)
    private String village;

    @Column(name = "taluka", length = 100)
    private String taluka;

    @Column(name = "district", length = 100)
    private String district;

    @Column(name = "state", length = 100)
    private String state;

    @Pattern(regexp = "^\\d{6}$", message = "Pincode must be 6 digits")
    @Column(name = "pincode", length = 6)
    private String pincode;

    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN number format")
    @Column(name = "pan_number", length = 10)
    private String panNumber;

    @Pattern(regexp = "^\\d{12}$", message = "Aadhar number must be 12 digits")
    @Column(name = "aadhar_number", length = 12)
    private String aadharNumber;

    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FarmerStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- Share Logic साठी लागणारे direct fields ---

    @Column(name = "has_7_12")
    private Boolean has712 = false; // Checkbox selected = true, Unselected = false

    @Column(name = "has_8a")
    private Boolean has8A = false;   // Checkbox selected = true, Unselected = false

    @Column(name = "farm_area")
    private Double farmArea = 0.0;   // एकर मधील जमीन (उदा. 4.5, 0.5)

    // --- Cascade Mappings ---

    @OneToOne(mappedBy = "farmer", cascade = CascadeType.ALL, orphanRemoval = true)
    private FarmerBankDetail bankDetail;

    @OneToOne(mappedBy = "farmer", cascade = CascadeType.ALL, orphanRemoval = true)
    private FarmerNominee nominee;

    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FarmerFarmDetail> farmDetails = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (registrationDate == null) {
            registrationDate = LocalDate.now();
        }
        if (status == null) {
            status = FarmerStatus.ACTIVE;
        }
        if (has712 == null) {
            has712 = false;
        }
        if (has8A == null) {
            has8A = false;
        }
        if (farmArea == null) {
            farmArea = 0.0;
        }
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
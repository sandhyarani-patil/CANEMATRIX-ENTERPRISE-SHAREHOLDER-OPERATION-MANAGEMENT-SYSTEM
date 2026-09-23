package com.example.farmer.canematrix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "share_allocations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Farmer Details ---
    @NotBlank(message = "Farmer Code is required")
    @Column(name = "farmer_code", nullable = false)
    private String farmerCode;

    @NotBlank(message = "Farmer Name is required")
    @Column(name = "farmer_name", nullable = false)
    private String farmerName;

    // --- Share Purchase Details ---
    @NotBlank(message = "Type of share is required")
    @Column(name = "type_of_share", nullable = false) // e.g., REGULAR, PROVISIONAL, NOMINAL
    private String typeOfShare;

    @NotNull(message = "Share purchased count is required")
    @Min(value = 1, message = "At least 1 share must be purchased")
    @Column(name = "share_purchased", nullable = false)
    private Integer sharePurchased;

    @NotNull(message = "Purchasing date is required")
    @Column(name = "purchasing_date", nullable = false)
    private LocalDate purchasingDate;

    @NotNull(message = "Rate per kg is required")
    @Column(name = "rate_per_kg", nullable = false)
    private BigDecimal ratePerKg;

    // --- Sugarcane & Production Details ---
    @NotBlank(message = "Type of sugarcane is required")
    @Column(name = "type_of_sugarcane", nullable = false) // e.g., Co 86032, CoM 0265
    private String typeOfSugarcane;

    @NotNull(message = "Planting date is required")
    @Column(name = "planting_date", nullable = false)
    private LocalDate plantingDate;

    @NotNull(message = "Per month sugar quota in kg is required")
    @Column(name = "per_month_sugar_kg", nullable = false)
    private Double perMonthSugarKg;

    // --- Administrative & Nominee Details ---
    //@NotBlank(message = "Nominee name is required")
    @Column(name = "nominee_name", nullable = false)
    private String nomineeName;

    @NotBlank(message = "Director name is required")
    @Column(name = "director_name", nullable = false)
    private String directorName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShareStatus status = ShareStatus.ACTIVE;

    @Column(name = "share_price", nullable = false)
    private BigDecimal sharePrice;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    public enum ShareStatus {
        ACTIVE,
        TRANSFERRED,
        CANCELLED
    }
}
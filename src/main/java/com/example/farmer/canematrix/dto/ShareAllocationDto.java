package com.example.farmer.canematrix.dto;

import com.example.farmer.canematrix.entity.ShareAllocation.ShareStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ShareAllocationDto {

    private Long id;

    @NotBlank(message = "Farmer Code is required")
    private String farmerCode;

    private String farmerName;


    private String typeOfShare;


    private BigDecimal sharePrice;


    private Integer sharePurchased;

    @NotNull(message = "Purchasing date is required")
    private LocalDate purchasingDate;


    private BigDecimal ratePerKg;

    @NotBlank(message = "Type of sugarcane is required")
    private String typeOfSugarcane;

    @NotNull(message = "Planting date is required")
    private LocalDate plantingDate;


    private Double perMonthSugarKg;

    // ➕ एकूण शेअर्सची फिक्स किंमत (Calculated)
    private BigDecimal totalPrice;

    private String nomineeName;

    @NotBlank(message = "Director name is required")
    private String directorName;

    private ShareStatus status = ShareStatus.ACTIVE;
}
package com.example.farmer.canematrix.dto;

import com.example.farmer.canematrix.entity.ShareSugarAllocation;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data // हे गेटर्स आणि सेटर्ससाठी आवश्यक आहे
@NoArgsConstructor
@AllArgsConstructor
public class ShareSugarAllocationDto {

    private Long id;

    @NotBlank(message = "Farmer code is required")
    private String farmerCode;

    private String farmerName;

    @NotBlank(message = "Allocation year is required")
    private String allocationYear;

    private Double perMonthSugarKg;
    private Double yearlySugarKg;
    private Double festivalSugarKg;
    private Double totalAllocatedSugarKg;
    private Double liftedSugarKg;
    private Double remainingSugarKg;

    // किंमतीचे फील्ड्स (DTO मध्ये पण असणे गरजेचे आहे)
    private BigDecimal ratePerKg;
    private BigDecimal yearlySugarPrice;
    private BigDecimal festivalSugarPrice;
    private BigDecimal totalBillAmount;

    private LocalDate lastUpdatedDate;
    private ShareSugarAllocation.SugarStatus status;
}
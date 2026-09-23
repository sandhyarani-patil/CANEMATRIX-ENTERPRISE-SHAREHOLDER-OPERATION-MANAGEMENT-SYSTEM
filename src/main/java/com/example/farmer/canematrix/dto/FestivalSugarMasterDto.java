package com.example.farmer.canematrix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class FestivalSugarMasterDto {

    private Long id;

    @NotBlank(message = "Allocation year is required")
    private String allocationYear;

    @NotBlank(message = "Festival name is required")
    private String festivalName;

    @NotNull(message = "Sugar quantity per farmer is required")
    private Double sugarQuantityPerFarmerKg;

    private LocalDate distributionStartDate;
}
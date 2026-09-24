package com.example.farmer.canematrix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TonnesSugarAllocationDto {

    private Long id;
    private String farmerCode;
    private String farmerName;

    private Double totalTonnes;          // एकूण घातलेला ऊस (Tonnes)
    private BigDecimal ratePerKg;        // प्रति किलो रेट

    private Double tonnesSugarKg;        // एकूण मिळणारी साखर
    private Double suppliedSugarKg;      // आतापर्यंत नेलेली एकूण साखर
    private Double remainingSugarKg;     // शिल्लक साखर
    private Double previousRemSugar;     // मागची शिल्लक साखर

    private BigDecimal amountOfTonnesSugar; // एकूण साखरेची किंमत / बिल अमाऊंट

    private LocalDate lastUpdatedDate;
    private String status;               // ACTIVE किंवा COMPLETED
}
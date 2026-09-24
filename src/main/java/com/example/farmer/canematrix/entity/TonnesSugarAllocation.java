package com.example.farmer.canematrix.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tonnes_sugar_allocation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TonnesSugarAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String farmerCode;
    private String farmerName;

    private Double totalTonnes;          // एकूण घातलेला ऊस (Tonnes)
    private BigDecimal ratePerKg;        // प्रति किलो रेट (Rates टेबलमधून येणार)

    private Double tonnesSugarKg;        // एकूण मिळणारी साखर (उदा. Total Tonnes नुसार ठरलेली)
    private Double suppliedSugarKg;      // आतापर्यंत नेलेली एकूण साखर
    private Double remainingSugarKg;     // शिल्लक साखर
    private Double previousRemSugar;     // मागची शिल्लक साखर

    private BigDecimal amountOfTonnesSugar; // एकूण साखरेची किंमत / बिल अमाऊंट

    private LocalDate lastUpdatedDate;

    @Enumerated(EnumType.STRING)
    private SugarStatus status = SugarStatus.ACTIVE;

    public enum SugarStatus {
        ACTIVE, COMPLETED
    }
}
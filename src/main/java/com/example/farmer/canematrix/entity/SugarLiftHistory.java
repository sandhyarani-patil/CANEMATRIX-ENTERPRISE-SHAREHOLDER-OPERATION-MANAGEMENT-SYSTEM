package com.example.farmer.canematrix.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sugar_lift_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SugarLiftHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String farmerCode;
    private String farmerName;

    private Double quantityLifted;         // या फेरीत नेलेली साखर
    private BigDecimal liftBillAmount;     // या फेरीतचे बिल
    private Double remainingSugarKg;       // <<-- हे नवीन फील्ड ॲड करा (त्या वेळेची शिल्लक साखर)
    private LocalDate liftDate;            // साखर नेल्याची तारीख
}
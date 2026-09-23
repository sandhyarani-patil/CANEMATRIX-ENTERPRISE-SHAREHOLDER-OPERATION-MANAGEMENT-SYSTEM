package com.example.farmer.canematrix.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "share_sugar_allocation")
@Data // हे सर्व Getters, Setters, toString, equals ऑटोमॅटिक जनरेट करते
@NoArgsConstructor
@AllArgsConstructor
public class ShareSugarAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String farmerCode;

    private String farmerName;

    private String allocationYear;

    // साखरेचे प्रमाण (Kg)
    private Double perMonthSugarKg;
    private Double yearlySugarKg;
    private Double festivalSugarKg;
    private Double totalAllocatedSugarKg;
    private Double liftedSugarKg = 0.0;
    private Double remainingSugarKg;

    // किंमती आणि बिलाचे फील्ड्स (ज्यामुळे एरर येत होती)
    private BigDecimal ratePerKg;
    private BigDecimal yearlySugarPrice;
    private BigDecimal festivalSugarPrice;
    private BigDecimal totalBillAmount;

    private LocalDate lastUpdatedDate;

    @Enumerated(EnumType.STRING)
    private SugarStatus status;

    public enum SugarStatus {
        ACTIVE,
        COMPLETED
    }
}
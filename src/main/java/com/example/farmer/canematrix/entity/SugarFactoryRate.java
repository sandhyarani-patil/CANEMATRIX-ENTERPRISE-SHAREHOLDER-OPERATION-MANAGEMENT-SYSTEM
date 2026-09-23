package com.example.farmer.canematrix.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sugar_factory_rate")
public class SugarFactoryRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sugarFactoryName; // साखर कारखान्याचे नाव

    @Column(nullable = false)
    private BigDecimal sharePurchaseAmount; // शेअर खरेदीची रक्कम (उदा. 10000.00)

    @Column(nullable = false)
    private Double perMonthShareSugar; // दरमहा मिळणारी शेअर साखर (उदा. 5.0 Kg)

    @Column(nullable = false)
    private BigDecimal rateOfShareSugar; // शेअर साखरेचा दर प्रति किलो (उदा. 11.00 Rs)

    @Column(nullable = false)
    private BigDecimal rateOfSugarcaneSugar; // उसाच्या टनेजवर मिळणाऱ्या साखरेचा दर प्रति किलो (उदा. 15.00 Rs)

    @Column(nullable = false)
    private BigDecimal rateOfSugarcanePerTon; // दर टन उसाचा खरेदी दर (उदा. 3100.00 Rs)

    @Column(nullable = false)
    private LocalDate updatedDate;

    // Default Constructor
    public SugarFactoryRate() {}

    // Parameterized Constructor
    public SugarFactoryRate(String sugarFactoryName, BigDecimal sharePurchaseAmount, Double perMonthShareSugar,
                            BigDecimal rateOfShareSugar, BigDecimal rateOfSugarcaneSugar,
                            BigDecimal rateOfSugarcanePerTon, LocalDate updatedDate) {
        this.sugarFactoryName = sugarFactoryName;
        this.sharePurchaseAmount = sharePurchaseAmount;
        this.perMonthShareSugar = perMonthShareSugar;
        this.rateOfShareSugar = rateOfShareSugar;
        this.rateOfSugarcaneSugar = rateOfSugarcaneSugar;
        this.rateOfSugarcanePerTon = rateOfSugarcanePerTon;
        this.updatedDate = updatedDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSugarFactoryName() { return sugarFactoryName; }
    public void setSugarFactoryName(String sugarFactoryName) { this.sugarFactoryName = sugarFactoryName; }

    public BigDecimal getSharePurchaseAmount() { return sharePurchaseAmount; }
    public void setSharePurchaseAmount(BigDecimal sharePurchaseAmount) { this.sharePurchaseAmount = sharePurchaseAmount; }

    public Double getPerMonthShareSugar() { return perMonthShareSugar; }
    public void setPerMonthShareSugar(Double perMonthShareSugar) { this.perMonthShareSugar = perMonthShareSugar; }

    public BigDecimal getRateOfShareSugar() { return rateOfShareSugar; }
    public void setRateOfShareSugar(BigDecimal rateOfShareSugar) { this.rateOfShareSugar = rateOfShareSugar; }

    public BigDecimal getRateOfSugarcaneSugar() { return rateOfSugarcaneSugar; }
    public void setRateOfSugarcaneSugar(BigDecimal rateOfSugarcaneSugar) { this.rateOfSugarcaneSugar = rateOfSugarcaneSugar; }

    public BigDecimal getRateOfSugarcanePerTon() { return rateOfSugarcanePerTon; }
    public void setRateOfSugarcanePerTon(BigDecimal rateOfSugarcanePerTon) { this.rateOfSugarcanePerTon = rateOfSugarcanePerTon; }

    public LocalDate getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDate updatedDate) { this.updatedDate = updatedDate; }
}
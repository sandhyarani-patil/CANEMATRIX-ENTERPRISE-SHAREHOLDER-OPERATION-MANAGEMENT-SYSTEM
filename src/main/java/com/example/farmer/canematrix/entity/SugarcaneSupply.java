package com.example.farmer.canematrix.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sugarcane_supplies")
@Data
public class SugarcaneSupply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String farmerCode;
    private String farmerName;

    private String farmCode;        // 👈 नवीन फील्ड
    private LocalDate plantingDate; // 👈 नवीन फील्ड

    private LocalDateTime supplyDate;
    private double tonnes;
    private double ratePerTon;
    private double totalPrice;

    private String tractorNumber;
    private String driverName;
    private String receiptNumber;

    private double cumulativeTonnes;
    private double cumulativeTotalPrice;
}
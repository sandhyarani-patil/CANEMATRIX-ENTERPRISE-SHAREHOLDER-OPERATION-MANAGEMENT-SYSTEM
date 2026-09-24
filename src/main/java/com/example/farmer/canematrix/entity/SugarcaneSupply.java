package com.example.farmer.canematrix.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "sugarcane_supplies")
@Data
public class SugarcaneSupply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String farmerCode;
    private String farmerName;

    private LocalDate supplyDate;
    private double tonnes;          // या खेपेचे टन
    private double ratePerTon;      // प्रति टन दर
    private double totalPrice;      // या खेपेचे एकूण बिल (tonnes * ratePerTon)

    private String tractorNumber;   // ट्रॅक्टर नंबर
    private String driverName;      // ड्रायव्हरचे नाव
    private String receiptNumber;
    // युनिक पावती नंबर

    private double cumulativeTonnes;      // आजवरचा एकूण टन (उदा. 15.5 + 15.5 = 31.0)
    private double cumulativeTotalPrice;  // आजवरची एकूण रक्कम
}
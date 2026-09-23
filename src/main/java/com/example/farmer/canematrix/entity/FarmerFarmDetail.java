package com.example.farmer.canematrix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "farmer_farm_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmerFarmDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @NotBlank(message = "Gat number / Survey number is required")
    @Column(name = "gat_number", nullable = false, length = 50)
    private String gatNumber;

    @NotNull(message = "Total area in acres is required")
    @Column(name = "total_area_acre", nullable = false)
    private Double totalAreaAcre;

    @Column(name = "sugarcane_area_acre")
    private Double sugarcaneAreaAcre;

    @Column(name = "irrigation_source", length = 50) // e.g., Well, Canal, Borewell, River
    private String irrigationSource;

    @Column(name = "village", length = 100)
    private String village;
}
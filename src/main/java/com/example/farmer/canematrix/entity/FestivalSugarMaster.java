package com.example.farmer.canematrix.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "festival_sugar_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FestivalSugarMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String allocationYear; // उदा. 2026-2027

    @Column(nullable = false)
    private String festivalName; // उदा. Diwali

    @Column(nullable = false)
    private Double sugarQuantityPerFarmerKg; // प्रत्येक शेतकऱ्याला मिळणारी साखर (उदा. 10.0 किलो)

    private LocalDate distributionStartDate; // वाटप सुरू करण्याची तारीख
}
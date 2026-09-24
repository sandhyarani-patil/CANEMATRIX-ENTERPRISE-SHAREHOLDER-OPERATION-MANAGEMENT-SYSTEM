package com.example.farmer.canematrix.repository;

import com.example.farmer.canematrix.entity.SugarcaneSupply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SugarcaneSupplyRepository extends JpaRepository<SugarcaneSupply, Long> {
    List<SugarcaneSupply> findByFarmerCode(String farmerCode);
    // 👉 नवीन: तारखेच्या रेंजनुसार (From Date to To Date) डेटा फिल्टर करणे
    List<SugarcaneSupply> findBySupplyDateBetween(LocalDate startDate, LocalDate endDate);
}
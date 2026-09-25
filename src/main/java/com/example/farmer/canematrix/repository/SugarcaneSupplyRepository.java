package com.example.farmer.canematrix.repository;

import com.example.farmer.canematrix.entity.SugarcaneSupply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SugarcaneSupplyRepository extends JpaRepository<SugarcaneSupply, Long> {
    List<SugarcaneSupply> findByFarmerCode(String farmerCode);

    List<SugarcaneSupply> findBySupplyDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // 👇 नवीन: ट्रॅक्टर नंबर आणि तारीख रेंजनुसार शोधण्यासाठी
    List<SugarcaneSupply> findByTractorNumberAndSupplyDateBetween(String tractorNumber, LocalDateTime startDate, LocalDateTime endDate);
}
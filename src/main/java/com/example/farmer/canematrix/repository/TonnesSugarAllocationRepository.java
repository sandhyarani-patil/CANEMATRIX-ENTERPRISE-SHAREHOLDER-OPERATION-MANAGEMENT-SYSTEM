package com.example.farmer.canematrix.repository;

import com.example.farmer.canematrix.entity.TonnesSugarAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TonnesSugarAllocationRepository extends JpaRepository<TonnesSugarAllocation, Long> {
    Optional<TonnesSugarAllocation> findByFarmerCode(String farmerCode);
}
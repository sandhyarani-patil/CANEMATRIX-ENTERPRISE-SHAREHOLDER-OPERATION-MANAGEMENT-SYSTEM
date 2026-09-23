package com.example.farmer.canematrix.repository;

import com.example.farmer.canematrix.entity.ShareSugarAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShareSugarAllocationRepository extends JpaRepository<ShareSugarAllocation, Long> {
    Optional<ShareSugarAllocation> findByFarmerCode(String farmerCode);
}
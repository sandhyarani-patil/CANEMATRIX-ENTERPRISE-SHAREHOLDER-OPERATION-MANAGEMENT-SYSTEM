package com.example.farmer.canematrix.repository;

import com.example.farmer.canematrix.entity.FarmerFarmDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmerFarmDetailRepository extends JpaRepository<FarmerFarmDetail, Long> {
    List<FarmerFarmDetail> findByFarmerId(Long farmerId);
}
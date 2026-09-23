package com.example.farmer.canematrix.repository;

import com.example.farmer.canematrix.entity.FarmerBankDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FarmerBankDetailRepository extends JpaRepository<FarmerBankDetail, Long> {
    Optional<FarmerBankDetail> findByFarmerId(Long farmerId);
}
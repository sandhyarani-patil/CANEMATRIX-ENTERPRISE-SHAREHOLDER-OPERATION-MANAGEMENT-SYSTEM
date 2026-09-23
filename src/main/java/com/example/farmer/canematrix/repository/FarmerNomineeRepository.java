package com.example.farmer.canematrix.repository;

import com.example.farmer.canematrix.entity.FarmerNominee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FarmerNomineeRepository extends JpaRepository<FarmerNominee, Long> {
    Optional<FarmerNominee> findByFarmerId(Long farmerId);
}
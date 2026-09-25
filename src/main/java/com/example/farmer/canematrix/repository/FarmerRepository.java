package com.example.farmer.canematrix.repository;

import com.example.farmer.canematrix.entity.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FarmerRepository extends JpaRepository<Farmer, Long> {

    Optional<Farmer> findByFarmerCode(String farmerCode);

    Optional<Farmer> findByMobileNumber(String mobileNumber);

    boolean existsByFarmerCode(String farmerCode);

    boolean existsByMobileNumber(String mobileNumber);


}
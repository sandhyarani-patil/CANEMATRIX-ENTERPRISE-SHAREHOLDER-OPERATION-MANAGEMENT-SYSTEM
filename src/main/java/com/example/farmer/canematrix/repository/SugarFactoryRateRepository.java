package com.example.farmer.canematrix.repository;

import com.example.farmer.canematrix.entity.SugarFactoryRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SugarFactoryRateRepository extends JpaRepository<SugarFactoryRate, Long> {

    // कारखान्याच्या नावाने रेट शोधण्यासाठी
    Optional<SugarFactoryRate> findBySugarFactoryName(String sugarFactoryName);

    // सर्वात शेवटचा अपडेट झालेला रेट शोधण्यासाठी
    Optional<SugarFactoryRate> findFirstByOrderByIdDesc();
}
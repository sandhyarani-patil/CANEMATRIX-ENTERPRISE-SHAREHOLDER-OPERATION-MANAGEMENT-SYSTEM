package com.example.farmer.canematrix.repository;


import com.example.farmer.canematrix.entity.ShareAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShareAllocationRepository extends JpaRepository<ShareAllocation, Long> {

    // Farmer Code नुसार लिस्ट शोधण्यासाठी
    List<ShareAllocation> findByFarmerCode(String farmerCode);

    // Sugarcane Type नुसार फिल्टर करण्यासाठी
    List<ShareAllocation> findByTypeOfSugarcane(String typeOfSugarcane);

    // Director Name नुसार रेकॉर्ड्स पाहण्यासाठी
    List<ShareAllocation> findByDirectorName(String directorName);

    // Farmer Code आधीच अस्तित्वात आहे का ते तपासण्यासाठी
    boolean existsByFarmerCode(String farmerCode);

}
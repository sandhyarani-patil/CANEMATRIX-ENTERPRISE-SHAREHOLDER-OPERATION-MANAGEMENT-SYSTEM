package com.example.farmer.canematrix.repository;

import com.example.farmer.canematrix.entity.FestivalSugarMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FestivalSugarMasterRepository extends JpaRepository<FestivalSugarMaster, Long> {
    // Jar varsha nusar kinva sanna nusar shodhache asel tar methods add karu shakto
}
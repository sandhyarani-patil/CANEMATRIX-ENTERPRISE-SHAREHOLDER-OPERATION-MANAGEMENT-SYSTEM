package com.example.farmer.canematrix.repository;

import com.example.farmer.canematrix.entity.SugarLiftHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SugarLiftHistoryRepository extends JpaRepository<SugarLiftHistory, Long> {
    List<SugarLiftHistory> findByFarmerCode(String farmerCode);
}
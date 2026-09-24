package com.example.farmer.canematrix.repository;

import com.example.farmer.canematrix.entity.TonnesSugarHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TonnesSugarHistoryRepository extends JpaRepository<TonnesSugarHistory, Long> {
    List<TonnesSugarHistory> findByFarmerCode(String farmerCode);
}
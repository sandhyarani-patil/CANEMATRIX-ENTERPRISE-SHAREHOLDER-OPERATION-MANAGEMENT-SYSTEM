package com.example.farmer.canematrix.repository;

import com.example.farmer.canematrix.entity.ShareTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShareTransferRepository extends JpaRepository<ShareTransfer, Long> {
    List<ShareTransfer> findByFarmerCode(String farmerCode);
}
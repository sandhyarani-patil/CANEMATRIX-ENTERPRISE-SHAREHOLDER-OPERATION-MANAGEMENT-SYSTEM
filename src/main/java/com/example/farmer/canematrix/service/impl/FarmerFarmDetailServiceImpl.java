package com.example.farmer.canematrix.service.impl;

import com.example.farmer.canematrix.entity.Farmer;
import com.example.farmer.canematrix.entity.FarmerFarmDetail;
import com.example.farmer.canematrix.exception.FarmerNotFoundException;
import com.example.farmer.canematrix.repository.FarmerFarmDetailRepository;
import com.example.farmer.canematrix.repository.FarmerRepository;
import com.example.farmer.canematrix.service.FarmerFarmDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FarmerFarmDetailServiceImpl implements FarmerFarmDetailService {

    private final FarmerFarmDetailRepository farmRepository;
    private final FarmerRepository farmerRepository;

    public FarmerFarmDetailServiceImpl(FarmerFarmDetailRepository farmRepository, FarmerRepository farmerRepository) {
        this.farmRepository = farmRepository;
        this.farmerRepository = farmerRepository;
    }

    @Override
    public FarmerFarmDetail addFarmDetail(Long farmerId, FarmerFarmDetail farmDetail) {
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new FarmerNotFoundException("Farmer not found with id: " + farmerId));

        farmDetail.setFarmer(farmer);
        return farmRepository.save(farmDetail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FarmerFarmDetail> getFarmsByFarmerId(Long farmerId) {
        return farmRepository.findByFarmerId(farmerId);
    }

    @Override
    public FarmerFarmDetail updateFarmDetail(Long farmId, FarmerFarmDetail updatedDetail) {
        FarmerFarmDetail existing = farmRepository.findById(farmId)
                .orElseThrow(() -> new RuntimeException("Farm detail not found with id: " + farmId));

        existing.setGatNumber(updatedDetail.getGatNumber());
        existing.setTotalAreaAcre(updatedDetail.getTotalAreaAcre());
        existing.setSugarcaneAreaAcre(updatedDetail.getSugarcaneAreaAcre());
        existing.setIrrigationSource(updatedDetail.getIrrigationSource());
        existing.setVillage(updatedDetail.getVillage());

        return farmRepository.save(existing);
    }

    @Override
    public void deleteFarmDetail(Long farmId) {
        if (!farmRepository.existsById(farmId)) {
            throw new RuntimeException("Farm detail not found with id: " + farmId);
        }
        farmRepository.deleteById(farmId);
    }
}
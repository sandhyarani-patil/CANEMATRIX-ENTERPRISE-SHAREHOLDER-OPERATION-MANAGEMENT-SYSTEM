package com.example.farmer.canematrix.service.impl;

import com.example.farmer.canematrix.dto.FestivalSugarMasterDto;
import com.example.farmer.canematrix.entity.FestivalSugarMaster;
import com.example.farmer.canematrix.entity.ShareSugarAllocation;
import com.example.farmer.canematrix.repository.FestivalSugarMasterRepository;
import com.example.farmer.canematrix.repository.ShareSugarAllocationRepository;
import com.example.farmer.canematrix.service.FestivalSugarMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FestivalSugarMasterServiceImpl implements FestivalSugarMasterService {

    @Autowired
    private FestivalSugarMasterRepository festivalRepository;

    @Autowired
    private ShareSugarAllocationRepository allocationRepository; // ऑटोमेशनसाठी इन्जेक्ट केले

    @Override
    @Transactional // डेटा सुरक्षित राहण्यासाठी (एरर आली तर रोलबॅक होईल)
    public FestivalSugarMasterDto createFestivalSugar(FestivalSugarMasterDto dto) {
        // 1. नवीन फेस्टिव्हल मास्टर सेव्ह करणे
        FestivalSugarMaster festival = new FestivalSugarMaster();
        festival.setAllocationYear(dto.getAllocationYear());
        festival.setFestivalName(dto.getFestivalName());
        festival.setSugarQuantityPerFarmerKg(dto.getSugarQuantityPerFarmerKg());
        festival.setDistributionStartDate(dto.getDistributionStartDate());

        FestivalSugarMaster savedFestival = festivalRepository.save(festival);

        // 2. [AUTOMATION] नवीन सण ॲड झाल्यावर सर्व शेतकऱ्यांच्या 'Share Sugar Allocation' मध्ये ती साखर आपोआप ॲड करणे
        List<ShareSugarAllocation> allAllocations = allocationRepository.findAll();

        for (ShareSugarAllocation allocation : allAllocations) {
            double currentFestivalSugar = allocation.getFestivalSugarKg() != null ? allocation.getFestivalSugarKg() : 0.0;
            double newlyAddedSugar = savedFestival.getSugarQuantityPerFarmerKg();

            double updatedFestivalTotal = currentFestivalSugar + newlyAddedSugar;
            allocation.setFestivalSugarKg(updatedFestivalTotal);

            // एकूण साखर (Yearly Sugar + Updated Festival Sugar) पुन्हा कॅल्क्युलेट करणे
            double newTotalAllocated = allocation.getYearlySugarKg() + updatedFestivalTotal;
            allocation.setTotalAllocatedSugarKg(newTotalAllocated);

            // शिल्लक साखर (Remaining Sugar) पुन्हा मोजणे
            double lifted = allocation.getLiftedSugarKg() != null ? allocation.getLiftedSugarKg() : 0.0;
            allocation.setRemainingSugarKg(newTotalAllocated - lifted);

            allocation.setLastUpdatedDate(LocalDate.now());
        }

        // सर्व शेतकऱ्यांचे अपडेटेड रेकॉर्ड्स एकाच वेळी सेव्ह करणे
        allocationRepository.saveAll(allAllocations);

        return mapToDto(savedFestival);
    }
    @Override
    @Transactional
    public FestivalSugarMasterDto updateFestivalSugar(Long id, FestivalSugarMasterDto dto) {
        // 1. आधी रेकॉर्ड आहे का ते तपासणे
        FestivalSugarMaster existing = festivalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Festival sugar not found with id: " + id));

        // 2. नवीन डेटा सेट करणे
        existing.setAllocationYear(dto.getAllocationYear());
        existing.setFestivalName(dto.getFestivalName());
        existing.setSugarQuantityPerFarmerKg(dto.getSugarQuantityPerFarmerKg());
        existing.setDistributionStartDate(dto.getDistributionStartDate());

        // 3. सेव्ह करणे
        FestivalSugarMaster updated = festivalRepository.save(existing);

        return mapToDto(updated);
    }
    @Override
    public List<FestivalSugarMasterDto> getAllFestivalSugars() {
        return festivalRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public FestivalSugarMasterDto getFestivalSugarById(Long id) {
        FestivalSugarMaster entity = festivalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Festival sugar not found with id: " + id));
        return mapToDto(entity);
    }

    @Override
    public void deleteFestivalSugar(Long id) {
        if (!festivalRepository.existsById(id)) {
            throw new RuntimeException("Festival sugar not found with id: " + id);
        }
        festivalRepository.deleteById(id);
    }

    private FestivalSugarMasterDto mapToDto(FestivalSugarMaster entity) {
        FestivalSugarMasterDto dto = new FestivalSugarMasterDto();
        dto.setId(entity.getId());
        dto.setAllocationYear(entity.getAllocationYear());
        dto.setFestivalName(entity.getFestivalName());
        dto.setSugarQuantityPerFarmerKg(entity.getSugarQuantityPerFarmerKg());
        dto.setDistributionStartDate(entity.getDistributionStartDate());
        return dto;
    }
}
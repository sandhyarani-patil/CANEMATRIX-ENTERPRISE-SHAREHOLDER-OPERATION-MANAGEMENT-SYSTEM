package com.example.farmer.canematrix.service.impl;

import com.example.farmer.canematrix.dto.ShareAllocationDto;
import com.example.farmer.canematrix.entity.Farmer;
import com.example.farmer.canematrix.entity.ShareAllocation;
import com.example.farmer.canematrix.entity.SugarFactoryRate;
import com.example.farmer.canematrix.exception.FarmerNotFoundException; // 👈 शेतकरी सापडला नाही तर
import com.example.farmer.canematrix.exception.ResourceNotFoundException; // 👈 रेकॉर्ड सापडला नाही तर
import com.example.farmer.canematrix.repository.FarmerRepository;
import com.example.farmer.canematrix.repository.ShareAllocationRepository;
import com.example.farmer.canematrix.repository.SugarFactoryRateRepository;
import com.example.farmer.canematrix.service.ShareAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShareAllocationServiceImpl implements ShareAllocationService {

    @Autowired
    private ShareAllocationRepository shareAllocationRepository;

    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private SugarFactoryRateRepository sugarFactoryRateRepository;

    @Override
    public ShareAllocationDto createShareAllocation(ShareAllocationDto dto) {

        // १. DB मधून शेतकऱ्याची माहिती आणा
        Farmer farmer = farmerRepository.findByFarmerCode(dto.getFarmerCode())
                .orElseThrow(() -> new FarmerNotFoundException("Farmer not found with code: " + dto.getFarmerCode()));

        ShareAllocation shareAllocation = new ShareAllocation();

        // ⭐ DB मधून थेट नाव आणि नॉमिनी ऑटोमॅटिक सेट करा (DTO वर अवलंबून नाही)
        shareAllocation.setFarmerName(farmer.getFarmerName());

        if (farmer.getNominee() != null) {
            shareAllocation.setNomineeName(farmer.getNominee().getNomineeName());
        } else {
            shareAllocation.setNomineeName("Not Available");
        }

        // इतर आवश्यक फील्ड्स DTO मधून घ्या
        shareAllocation.setPurchasingDate(dto.getPurchasingDate());
        shareAllocation.setTypeOfSugarcane(dto.getTypeOfSugarcane());
        shareAllocation.setPlantingDate(dto.getPlantingDate());
        shareAllocation.setDirectorName(dto.getDirectorName());
        shareAllocation.setStatus(dto.getStatus() != null ? dto.getStatus() : ShareAllocation.ShareStatus.ACTIVE);

        // २. 'A' Class साठी पात्रता तपासा (जमीन >= 0.5 एकर आणि 7/12 & 8A जोडलेले असणे)
        boolean isEligibleForClassA = Boolean.TRUE.equals(farmer.getHas712())
                && Boolean.TRUE.equals(farmer.getHas8A())
                && farmer.getFarmArea() != null
                && farmer.getFarmArea() >= 0.5;

        if (isEligibleForClassA) {
            // === TYPE A MEMBER ===
            shareAllocation.setTypeOfShare("A");
            shareAllocation.setFarmerCode("A-" + farmer.getFarmerCode());
            shareAllocation.setSharePurchased(1);

            SugarFactoryRate latestRate = sugarFactoryRateRepository.findFirstByOrderByIdDesc()
                    .orElseThrow(() -> new ResourceNotFoundException("Factory rates are not configured in Rate Master!"));

            shareAllocation.setSharePrice(latestRate.getSharePurchaseAmount());
            shareAllocation.setTotalPrice(latestRate.getSharePurchaseAmount().multiply(BigDecimal.valueOf(shareAllocation.getSharePurchased())));

            // Rate Master मधील अद्ययावत Valuation Field नुसार Mapping
            shareAllocation.setPerMonthSugarKg(latestRate.getPerMonthShareSugar());
            shareAllocation.setRatePerKg(latestRate.getRateOfShareSugar()); // शेअर साखरेचा दर (उदा. ₹11.00)

        } else {
            // === TYPE C MEMBER ===
            shareAllocation.setTypeOfShare("C");
            shareAllocation.setFarmerCode("C-" + farmer.getFarmerCode());
            shareAllocation.setSharePurchased(1);

            shareAllocation.setSharePrice(BigDecimal.ZERO);
            shareAllocation.setTotalPrice(BigDecimal.ZERO);
            shareAllocation.setPerMonthSugarKg(0.0);
            shareAllocation.setRatePerKg(BigDecimal.ZERO);
        }

        ShareAllocation savedAllocation = shareAllocationRepository.save(shareAllocation);
        return mapToDto(savedAllocation);
    }

    @Override
    public List<ShareAllocationDto> getAllShareAllocations() {
        return shareAllocationRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ShareAllocationDto getShareAllocationById(Long id) {
        ShareAllocation existing = shareAllocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Share Allocation not found with id: " + id));
        return mapToDto(existing);
    }

    @Override
    public List<ShareAllocationDto> getShareAllocationsByFarmerCode(String farmerCode) {
        return shareAllocationRepository.findByFarmerCode(farmerCode)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ShareAllocationDto updateShareAllocation(Long id, ShareAllocationDto dto) {
        ShareAllocation existing = shareAllocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Share Allocation not found with id: " + id));

        existing.setPurchasingDate(dto.getPurchasingDate());
        existing.setTypeOfSugarcane(dto.getTypeOfSugarcane());
        existing.setPlantingDate(dto.getPlantingDate());
        existing.setDirectorName(dto.getDirectorName());
        existing.setStatus(dto.getStatus());

        if ("C".equalsIgnoreCase(existing.getTypeOfShare())) {
            existing.setSharePurchased(1);
            existing.setSharePrice(BigDecimal.ZERO);
            existing.setTotalPrice(BigDecimal.ZERO);
            existing.setPerMonthSugarKg(0.0);
            existing.setRatePerKg(BigDecimal.ZERO);
        } else {
            SugarFactoryRate latestRate = sugarFactoryRateRepository.findFirstByOrderByIdDesc()
                    .orElseThrow(() -> new ResourceNotFoundException("Factory rates are not configured in Rate Master!"));

            existing.setSharePurchased(1);
            existing.setSharePrice(latestRate.getSharePurchaseAmount());
            existing.setTotalPrice(latestRate.getSharePurchaseAmount());
            existing.setPerMonthSugarKg(latestRate.getPerMonthShareSugar());
            existing.setRatePerKg(latestRate.getRateOfShareSugar());
        }

        ShareAllocation updatedAllocation = shareAllocationRepository.save(existing);
        return mapToDto(updatedAllocation);
    }

    @Override
    public void deleteShareAllocation(Long id) {
        if (!shareAllocationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Share Allocation not found with id: " + id);
        }
        shareAllocationRepository.deleteById(id);
    }

    // === Helper Methods for DTO Mapping ===

    private ShareAllocation mapToEntity(ShareAllocationDto dto) {
        ShareAllocation entity = new ShareAllocation();
        entity.setId(dto.getId());
        entity.setFarmerCode(dto.getFarmerCode());
        entity.setFarmerName(dto.getFarmerName());
        entity.setTypeOfShare(dto.getTypeOfShare());
        entity.setSharePurchased(dto.getSharePurchased());
        entity.setSharePrice(dto.getSharePrice());
        entity.setTotalPrice(dto.getTotalPrice());
        entity.setPurchasingDate(dto.getPurchasingDate());
        entity.setRatePerKg(dto.getRatePerKg());
        entity.setTypeOfSugarcane(dto.getTypeOfSugarcane());
        entity.setPlantingDate(dto.getPlantingDate());
        entity.setPerMonthSugarKg(dto.getPerMonthSugarKg());
        entity.setNomineeName(dto.getNomineeName());
        entity.setDirectorName(dto.getDirectorName());
        entity.setStatus(dto.getStatus());
        return entity;
    }

    private ShareAllocationDto mapToDto(ShareAllocation entity) {
        ShareAllocationDto dto = new ShareAllocationDto();
        dto.setId(entity.getId());
        dto.setFarmerCode(entity.getFarmerCode());
        dto.setFarmerName(entity.getFarmerName());
        dto.setTypeOfShare(entity.getTypeOfShare());
        dto.setSharePurchased(entity.getSharePurchased());
        dto.setSharePrice(entity.getSharePrice());
        dto.setTotalPrice(entity.getTotalPrice());
        dto.setPurchasingDate(entity.getPurchasingDate());
        dto.setRatePerKg(entity.getRatePerKg());
        dto.setTypeOfSugarcane(dto.getTypeOfSugarcane());
        dto.setPlantingDate(dto.getPlantingDate());
        dto.setPerMonthSugarKg(dto.getPerMonthSugarKg());
        dto.setNomineeName(entity.getNomineeName());
        dto.setDirectorName(entity.getDirectorName());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
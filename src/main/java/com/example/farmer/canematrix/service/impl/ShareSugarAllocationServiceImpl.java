package com.example.farmer.canematrix.service.impl;

import com.example.farmer.canematrix.dto.ShareSugarAllocationDto;
import com.example.farmer.canematrix.entity.ShareSugarAllocation;
import com.example.farmer.canematrix.entity.SugarFactoryRate;
import com.example.farmer.canematrix.entity.FestivalSugarMaster;
import com.example.farmer.canematrix.repository.ShareSugarAllocationRepository;
import com.example.farmer.canematrix.repository.SugarFactoryRateRepository;
import com.example.farmer.canematrix.repository.FestivalSugarMasterRepository;
import com.example.farmer.canematrix.repository.FarmerRepository;
import com.example.farmer.canematrix.service.ShareSugarAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShareSugarAllocationServiceImpl implements ShareSugarAllocationService {

    @Autowired
    private ShareSugarAllocationRepository allocationRepository;

    @Autowired
    private SugarFactoryRateRepository rateRepository;

    @Autowired
    private FestivalSugarMasterRepository festivalRepository;

    @Autowired
    private FarmerRepository farmerRepository;

    @Override
    public ShareSugarAllocationDto createShareSugarAllocation(ShareSugarAllocationDto requestDto) {
        // 1. Farmer Code वरून नाव ऑटो फेच करणे
        var farmer = farmerRepository.findByFarmerCode(requestDto.getFarmerCode())
                .orElseThrow(() -> new RuntimeException("Farmer not found with code: " + requestDto.getFarmerCode()));

        ShareSugarAllocation allocation = new ShareSugarAllocation();
        allocation.setFarmerCode(farmer.getFarmerCode());
        allocation.setFarmerName(farmer.getFarmerName());
        allocation.setAllocationYear(requestDto.getAllocationYear());
        allocation.setStatus(ShareSugarAllocation.SugarStatus.ACTIVE);
        allocation.setLastUpdatedDate(LocalDate.now());

        // 2. Rate Master मधून दर (Rate per Kg) घेणे
        SugarFactoryRate latestRate = rateRepository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new RuntimeException("Factory rates are not configured in Rate Master!"));

        BigDecimal ratePerKg = latestRate.getRateOfShareSugar() != null ? latestRate.getRateOfShareSugar() : BigDecimal.ZERO;
        allocation.setRatePerKg(ratePerKg);

        // 3. मासिक ते वार्षिक साखर कॅल्क्युलेशन (perMonth * 12)
        double monthlySugar = latestRate.getPerMonthShareSugar() != null ? latestRate.getPerMonthShareSugar() : 5.0;
        allocation.setPerMonthSugarKg(monthlySugar);
        double yearlySugar = monthlySugar * 12;
        allocation.setYearlySugarKg(yearlySugar);

        // 4. Festival Sugar Master मधून सणाची साखर घेणे
        double festivalSugar = festivalRepository.findAll().stream()
                .mapToDouble(FestivalSugarMaster::getSugarQuantityPerFarmerKg)
                .sum(); // सर्व ॲक्टिव्ह सणांची बेरीज किंवा हवी असल्यास फिल्टर करू शकता

        allocation.setFestivalSugarKg(festivalSugar);

        // 5. Total Allocated & Remaining Weight Calculation
        double totalAllocated = yearlySugar + festivalSugar;
        allocation.setTotalAllocatedSugarKg(totalAllocated);
        allocation.setLiftedSugarKg(0.0);
        allocation.setRemainingSugarKg(totalAllocated);

        // 6. PRICE & BILL CALCULATIONS (सण साखर पूर्णपणे मोफत = 0 रुपये)
        BigDecimal yearlyPrice = ratePerKg.multiply(BigDecimal.valueOf(yearlySugar));
        BigDecimal festivalPrice = BigDecimal.ZERO; // फ्री असल्यामुळे 0

        allocation.setYearlySugarPrice(yearlyPrice);
        allocation.setFestivalSugarPrice(festivalPrice);
        allocation.setTotalBillAmount(yearlyPrice.add(festivalPrice));

        ShareSugarAllocation saved = allocationRepository.save(allocation);
        return mapToDto(saved);
    }

    @Override
    public List<ShareSugarAllocationDto> getAllShareSugarAllocations() {
        return allocationRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ShareSugarAllocationDto getShareSugarAllocationByFarmerCode(String farmerCode) {
        ShareSugarAllocation allocation = allocationRepository.findByFarmerCode(farmerCode)
                .orElseThrow(() -> new RuntimeException("Share Sugar Allocation not found for farmer code: " + farmerCode));
        return mapToDto(allocation);
    }

    @Override
    public void deleteShareSugarAllocation(Long id) {
        if (!allocationRepository.existsById(id)) {
            throw new RuntimeException("Record not found with ID: " + id);
        }
        allocationRepository.deleteById(id);
    }

    private ShareSugarAllocationDto mapToDto(ShareSugarAllocation entity) {
        ShareSugarAllocationDto dto = new ShareSugarAllocationDto();
        dto.setId(entity.getId());
        dto.setFarmerCode(entity.getFarmerCode());
        dto.setFarmerName(entity.getFarmerName());
        dto.setAllocationYear(entity.getAllocationYear());
        dto.setPerMonthSugarKg(entity.getPerMonthSugarKg());
        dto.setYearlySugarKg(entity.getYearlySugarKg());
        dto.setFestivalSugarKg(entity.getFestivalSugarKg());
        dto.setTotalAllocatedSugarKg(entity.getTotalAllocatedSugarKg());
        dto.setLiftedSugarKg(entity.getLiftedSugarKg());
        dto.setRemainingSugarKg(entity.getRemainingSugarKg());
        dto.setRatePerKg(entity.getRatePerKg());
        dto.setYearlySugarPrice(entity.getYearlySugarPrice());
        dto.setFestivalSugarPrice(entity.getFestivalSugarPrice());
        dto.setTotalBillAmount(entity.getTotalBillAmount());
        dto.setLastUpdatedDate(entity.getLastUpdatedDate());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
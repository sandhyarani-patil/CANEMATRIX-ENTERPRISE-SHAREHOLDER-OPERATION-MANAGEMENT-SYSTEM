package com.example.farmer.canematrix.service.impl;

import com.example.farmer.canematrix.dto.ShareSugarAllocationDto;
import com.example.farmer.canematrix.dto.SugarLiftReceiptDto;
import com.example.farmer.canematrix.entity.*;
import com.example.farmer.canematrix.repository.*;
import com.example.farmer.canematrix.service.ShareSugarAllocationService;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    @Autowired
    private SugarLiftHistoryRepository historyRepository;

    @Autowired
    private ShareAllocationRepository shareAllocationRepository;

    @Override
    public ShareSugarAllocationDto createShareSugarAllocation(ShareSugarAllocationDto requestDto) {

        List<ShareAllocation> shareAllocations = shareAllocationRepository.findByFarmerCode(requestDto.getFarmerCode());

        if (shareAllocations == null || shareAllocations.isEmpty()) {
            throw new RuntimeException("Share Allocation not found! Farmer must complete Share Allocation first.");
        }

        ShareAllocation shareAllocationCheck = shareAllocations.get(0);

        if (!"A".equalsIgnoreCase(shareAllocationCheck.getTypeOfShare())) {
            throw new RuntimeException("Error: Share sugar allocation is only allowed for 'A' series farmers! This farmer is a 'C' series member.");
        }

        String incomingCode = requestDto.getFarmerCode();
        String originalFarmerCode = incomingCode;

        if (incomingCode != null && (incomingCode.startsWith("A-") || incomingCode.startsWith("C-") || incomingCode.startsWith("a-") || incomingCode.startsWith("c-"))) {
            originalFarmerCode = incomingCode.substring(2);
        }

        var farmer = farmerRepository.findByFarmerCode(originalFarmerCode)
                .orElseThrow(() -> new RuntimeException("Farmer not found with code: " + incomingCode));

        ShareSugarAllocation allocation = new ShareSugarAllocation();
        allocation.setFarmerCode(shareAllocationCheck.getFarmerCode());
        allocation.setFarmerName(farmer.getFarmerName());
        allocation.setAllocationYear(requestDto.getAllocationYear());
        allocation.setStatus(ShareSugarAllocation.SugarStatus.ACTIVE);
        allocation.setLastUpdatedDate(LocalDate.now());

        SugarFactoryRate latestRate = rateRepository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new RuntimeException("Factory rates are not configured in Rate Master!"));

        BigDecimal ratePerKg = latestRate.getRateOfShareSugar() != null ? latestRate.getRateOfShareSugar() : BigDecimal.ZERO;
        allocation.setRatePerKg(ratePerKg);

        double monthlySugar = latestRate.getPerMonthShareSugar() != null ? latestRate.getPerMonthShareSugar() : 5.0;
        allocation.setPerMonthSugarKg(monthlySugar);
        double yearlySugar = monthlySugar * 12;
        allocation.setYearlySugarKg(yearlySugar);

        double festivalSugar = festivalRepository.findAll().stream()
                .mapToDouble(FestivalSugarMaster::getSugarQuantityPerFarmerKg)
                .sum();

        allocation.setFestivalSugarKg(festivalSugar);

        double totalAllocated = yearlySugar + festivalSugar;
        allocation.setTotalAllocatedSugarKg(totalAllocated);
        allocation.setLiftedSugarKg(0.0);
        allocation.setRemainingSugarKg(totalAllocated);

        BigDecimal yearlyPrice = ratePerKg.multiply(BigDecimal.valueOf(yearlySugar));
        BigDecimal festivalPrice = BigDecimal.ZERO;

        allocation.setYearlySugarPrice(yearlyPrice);
        allocation.setFestivalSugarPrice(festivalPrice);
        allocation.setTotalBillAmount(BigDecimal.ZERO);

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
    public SugarLiftReceiptDto liftSugar(String farmerCode, double quantityToLift) {

        ShareSugarAllocation allocation = allocationRepository.findByFarmerCode(farmerCode)
                .orElseThrow(() -> new RuntimeException("Allocation not found for farmer code: " + farmerCode));

        if (farmerCode != null && farmerCode.toUpperCase().startsWith("C-")) {
            throw new RuntimeException("Error: Share sugar is only applicable for 'A' series farmers! C-series farmers cannot lift share sugar.");
        }

        double currentRemaining = allocation.getRemainingSugarKg();
        if (quantityToLift > currentRemaining) {
            throw new RuntimeException("Error: Cannot lift " + quantityToLift + " kg. Only " + currentRemaining + " kg is remaining!");
        }

        double previousLifted = allocation.getLiftedSugarKg() != null ? allocation.getLiftedSugarKg() : 0.0;
        double newTotalLifted = previousLifted + quantityToLift;
        allocation.setLiftedSugarKg(newTotalLifted);

        double totalAllocated = allocation.getTotalAllocatedSugarKg();
        double newRemaining = totalAllocated - newTotalLifted;
        allocation.setRemainingSugarKg(newRemaining);

        BigDecimal rate = allocation.getRatePerKg() != null ? allocation.getRatePerKg() : BigDecimal.ZERO;
        BigDecimal currentLiftBill = rate.multiply(BigDecimal.valueOf(quantityToLift));

        BigDecimal totalLiftedPrice = rate.multiply(BigDecimal.valueOf(newTotalLifted));
        allocation.setTotalBillAmount(totalLiftedPrice);

        if (newRemaining == 0.0) {
            allocation.setStatus(ShareSugarAllocation.SugarStatus.COMPLETED);
        }

        allocation.setLastUpdatedDate(LocalDate.now());
        allocationRepository.save(allocation);

        SugarLiftHistory history = new SugarLiftHistory();
        history.setFarmerCode(allocation.getFarmerCode());
        history.setFarmerName(allocation.getFarmerName());
        history.setQuantityLifted(quantityToLift);
        history.setLiftBillAmount(currentLiftBill);
        history.setRemainingSugarKg(newRemaining);
        history.setLiftDate(LocalDate.now());
        historyRepository.save(history);

        SugarLiftReceiptDto receipt = new SugarLiftReceiptDto();
        receipt.setFarmerCode(allocation.getFarmerCode());
        receipt.setFarmerName(allocation.getFarmerName());
        receipt.setAllocationYear(allocation.getAllocationYear());
        receipt.setCurrentLiftedKg(quantityToLift);
        receipt.setRatePerKg(rate);
        receipt.setCurrentBillAmount(currentLiftBill);
        receipt.setTotalLiftedSoFar(newTotalLifted);
        receipt.setRemainingSugarKg(newRemaining);
        receipt.setLiftDate(LocalDate.now());
        receipt.setStatus(allocation.getStatus().name());

        return receipt;
    }

    @Override
    public byte[] generateLiftReceiptPdf(SugarLiftReceiptDto receipt) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A5);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contentStream.newLineAtOffset(30, 400);
                contentStream.showText("SUGAR FACTORY DISTRIBUTION SYSTEM");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
                contentStream.newLineAtOffset(0, -18);
                contentStream.showText("Sugar Lifting Receipt");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("--------------------------------------------------------------------------------------------------");

                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Date : " + receipt.getLiftDate());

                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Farmer Code : " + receipt.getFarmerCode());

                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Farmer Name : " + receipt.getFarmerName());

                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Allocation Year : " + receipt.getAllocationYear());

                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("--------------------------------------------------------------------------------------------------");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 9);
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Current Lifted Qty : " + receipt.getCurrentLiftedKg() + " Kg");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Rate Per Kg        : Rs. " + receipt.getRatePerKg());

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 9);
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Current Bill Amount: Rs. " + receipt.getCurrentBillAmount());

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("--------------------------------------------------------------------------------------------------");

                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Total Lifted So Far: " + receipt.getTotalLiftedSoFar() + " Kg");

                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Remaining Balance  : " + receipt.getRemainingSugarKg() + " Kg");

                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Status             : " + receipt.getStatus());

                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("==========================================================");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Thank You! Visit Again.");

                contentStream.endText();
            }

            document.save(out);
        } catch (IOException e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out.toByteArray();
    }

    @Override
    public List<SugarLiftHistory> getFarmerLiftHistory(String farmerCode) {
        return historyRepository.findByFarmerCode(farmerCode);
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
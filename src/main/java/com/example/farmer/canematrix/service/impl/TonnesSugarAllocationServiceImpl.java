package com.example.farmer.canematrix.service.impl;

import com.example.farmer.canematrix.dto.TonnesSugarReceiptDto;
import com.example.farmer.canematrix.entity.Farmer;
import com.example.farmer.canematrix.entity.SugarFactoryRate;
import com.example.farmer.canematrix.entity.TonnesSugarAllocation;
import com.example.farmer.canematrix.entity.TonnesSugarHistory;
import com.example.farmer.canematrix.exception.FarmerNotFoundException;     // 👈 शेतकरी सापडला नाही तर
import com.example.farmer.canematrix.exception.ResourceNotFoundException; // 👈 रेकॉर्ड किंवा रेट सापडला नाही तर
import com.example.farmer.canematrix.repository.FarmerRepository;
import com.example.farmer.canematrix.repository.SugarFactoryRateRepository;
import com.example.farmer.canematrix.repository.TonnesSugarAllocationRepository;
import com.example.farmer.canematrix.repository.TonnesSugarHistoryRepository;
import com.example.farmer.canematrix.service.TonnesSugarAllocationService;

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

@Service
public class TonnesSugarAllocationServiceImpl implements TonnesSugarAllocationService {

    @Autowired
    private TonnesSugarAllocationRepository allocationRepository;

    @Autowired
    private TonnesSugarHistoryRepository historyRepository;

    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private SugarFactoryRateRepository rateRepository;

    // 🛠️ एक कॉमन मेथड प्रीफिक्स काढण्यासाठी (हायफन आणि अँडरस्कोर दोन्ही चालतील)
    private String extractOriginalCode(String farmerCode) {
        if (farmerCode != null && farmerCode.length() > 2) {
            String prefix = farmerCode.substring(0, 2).toUpperCase();
            if (prefix.equals("A-") || prefix.equals("C-") || prefix.equals("A_") || prefix.equals("C_")) {
                return farmerCode.substring(2);
            }
        }
        return farmerCode;
    }

    @Override
    public TonnesSugarAllocation createAllocation(String farmerCode, Double totalTonnes) {

        String originalFarmerCode = extractOriginalCode(farmerCode);

        Farmer farmer = farmerRepository.findByFarmerCode(originalFarmerCode)
                .orElseThrow(() -> new FarmerNotFoundException("Farmer not found with code: " + originalFarmerCode));

        SugarFactoryRate latestRate = rateRepository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new ResourceNotFoundException("Factory rates are not configured in Rate Master!"));

        BigDecimal ratePerKg = latestRate.getRateOfSugarcaneSugar() != null ? latestRate.getRateOfSugarcaneSugar() : BigDecimal.ZERO;

        TonnesSugarAllocation allocation = new TonnesSugarAllocation();
        allocation.setFarmerCode(originalFarmerCode);
        allocation.setFarmerName(farmer.getFarmerName());
        allocation.setTotalTonnes(totalTonnes);
        allocation.setRatePerKg(ratePerKg);

        double calculatedSugar = totalTonnes * 1.0;
        allocation.setTonnesSugarKg(calculatedSugar);
        allocation.setSuppliedSugarKg(0.0);
        allocation.setPreviousRemSugar(calculatedSugar);
        allocation.setRemainingSugarKg(calculatedSugar);

        BigDecimal totalAmount = ratePerKg.multiply(BigDecimal.valueOf(calculatedSugar));
        allocation.setAmountOfTonnesSugar(totalAmount);

        allocation.setLastUpdatedDate(LocalDate.now());
        allocation.setStatus(TonnesSugarAllocation.SugarStatus.ACTIVE);

        return allocationRepository.save(allocation);
    }

    @Override
    public TonnesSugarReceiptDto liftTonnesSugar(String farmerCode, double quantityToLift) {

        String originalFarmerCode = extractOriginalCode(farmerCode);

        TonnesSugarAllocation allocation = allocationRepository.findByFarmerCode(originalFarmerCode)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation not found for farmer code: " + originalFarmerCode));

        double currentRemaining = allocation.getRemainingSugarKg();
        if (quantityToLift > currentRemaining) {
            throw new IllegalArgumentException("Error: Cannot lift " + quantityToLift + " kg. Only " + currentRemaining + " kg is remaining!");
        }

        double previousRem = allocation.getRemainingSugarKg();
        allocation.setPreviousRemSugar(previousRem);

        double suppliedSoFar = allocation.getSuppliedSugarKg() != null ? allocation.getSuppliedSugarKg() : 0.0;
        double newSupplied = suppliedSoFar + quantityToLift;
        allocation.setSuppliedSugarKg(newSupplied);

        double newRemaining = allocation.getTonnesSugarKg() - newSupplied;
        allocation.setRemainingSugarKg(newRemaining);

        BigDecimal rate = allocation.getRatePerKg() != null ? allocation.getRatePerKg() : BigDecimal.ZERO;
        BigDecimal currentBill = rate.multiply(BigDecimal.valueOf(quantityToLift));

        if (newRemaining == 0.0) {
            allocation.setStatus(TonnesSugarAllocation.SugarStatus.COMPLETED);
        }

        allocation.setLastUpdatedDate(LocalDate.now());
        allocationRepository.save(allocation);

        TonnesSugarHistory history = new TonnesSugarHistory();
        history.setFarmerCode(allocation.getFarmerCode());
        history.setFarmerName(allocation.getFarmerName());
        history.setQuantityLifted(quantityToLift);
        history.setLiftBillAmount(currentBill);
        history.setRemainingSugarKg(newRemaining);
        history.setLiftDate(LocalDate.now());
        historyRepository.save(history);

        TonnesSugarReceiptDto receipt = new TonnesSugarReceiptDto();

        receipt.setLiftHistoryId(history.getId()); // 👈 हा भाग ॲड करा (history ID set करण्यासाठी)
        receipt.setFarmerCode(allocation.getFarmerCode());
        receipt.setFarmerName(allocation.getFarmerName());
        receipt.setTotalTonnes(allocation.getTotalTonnes());
        receipt.setRatePerKg(rate);
        receipt.setTonnesSugarKg(allocation.getTonnesSugarKg());
        receipt.setSuppliedSugarKg(newSupplied);
        receipt.setPreviousRemSugar(previousRem);
        receipt.setRemainingSugarKg(newRemaining);
        receipt.setAmountOfTonnesSugar(allocation.getAmountOfTonnesSugar());
        receipt.setCurrentLiftedKg(quantityToLift);
        receipt.setCurrentBillAmount(currentBill);
        receipt.setLiftDate(LocalDate.now());
        receipt.setStatus(allocation.getStatus().name());

        return receipt;
    }

    @Override
    public byte[] generateTonnesReceiptPdf(TonnesSugarReceiptDto receipt) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A5);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contentStream.newLineAtOffset(30, 400);
                contentStream.showText("SUGAR FACTORY - CANE SUGAR RECEIPT");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("--------------------------------------------------------------------------------------------------");

                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Date : " + receipt.getLiftDate());

                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Farmer Code : " + receipt.getFarmerCode());

                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Farmer Name : " + receipt.getFarmerName());

                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Total Tonnes Cane : " + receipt.getTotalTonnes() + " Tonnes");

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
                contentStream.showText("Total Sugar Allocated : " + receipt.getTonnesSugarKg() + " Kg");

                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Supplied So Far       : " + receipt.getSuppliedSugarKg() + " Kg");

                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Previous Rem. Sugar   : " + receipt.getPreviousRemSugar() + " Kg");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 9);
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Remaining Balance     : " + receipt.getRemainingSugarKg() + " Kg");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Status                : " + receipt.getStatus());

                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("==========================================================");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Thank You! Visit Again.");

                contentStream.endText();
            }

            document.save(out);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error while generating PDF", e);
        }

        return out.toByteArray();
    }
    @Override
    public byte[] generateTonnesReceiptPdfByHistoryId(Long historyId) {
        TonnesSugarHistory history = historyRepository.findById(historyId)
                .orElseThrow(() -> new ResourceNotFoundException("Lift history not found: " + historyId));

        TonnesSugarAllocation allocation = allocationRepository.findByFarmerCode(history.getFarmerCode())
                .orElseThrow(() -> new ResourceNotFoundException("Allocation not found for: " + history.getFarmerCode()));

        BigDecimal rate = allocation.getRatePerKg() != null ? allocation.getRatePerKg() : BigDecimal.ZERO;

        TonnesSugarReceiptDto receipt = new TonnesSugarReceiptDto();
        receipt.setLiftHistoryId(history.getId());
        receipt.setFarmerCode(history.getFarmerCode());
        receipt.setFarmerName(history.getFarmerName());
        receipt.setTotalTonnes(allocation.getTotalTonnes());
        receipt.setRatePerKg(rate);
        receipt.setTonnesSugarKg(allocation.getTonnesSugarKg());
        receipt.setSuppliedSugarKg(allocation.getSuppliedSugarKg());
        receipt.setRemainingSugarKg(history.getRemainingSugarKg());
        receipt.setPreviousRemSugar(allocation.getPreviousRemSugar());
        receipt.setAmountOfTonnesSugar(allocation.getAmountOfTonnesSugar());
        receipt.setCurrentLiftedKg(history.getQuantityLifted());
        receipt.setCurrentBillAmount(history.getLiftBillAmount());
        receipt.setLiftDate(history.getLiftDate());
        receipt.setStatus(allocation.getStatus().name());

        return generateTonnesReceiptPdf(receipt);
    }
    @Override
    public List<TonnesSugarAllocation> getAllAllocations() {
        return allocationRepository.findAll();
    }

    @Override
    public List<TonnesSugarHistory> getFarmerTonnesHistory(String farmerCode) {
        String originalFarmerCode = extractOriginalCode(farmerCode);
        return historyRepository.findByFarmerCode(originalFarmerCode);
    }
}
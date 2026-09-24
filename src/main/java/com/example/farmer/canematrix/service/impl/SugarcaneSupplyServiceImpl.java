package com.example.farmer.canematrix.service.impl;

import com.example.farmer.canematrix.dto.FarmerSupplySummaryDTO;
import com.example.farmer.canematrix.entity.Farmer;
import com.example.farmer.canematrix.entity.SugarFactoryRate;
import com.example.farmer.canematrix.entity.SugarcaneSupply;
import com.example.farmer.canematrix.repository.FarmerRepository;
import com.example.farmer.canematrix.repository.SugarFactoryRateRepository;
import com.example.farmer.canematrix.repository.SugarcaneSupplyRepository;
import com.example.farmer.canematrix.service.SugarcaneSupplyService;

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
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class SugarcaneSupplyServiceImpl implements SugarcaneSupplyService {

    @Autowired
    private SugarcaneSupplyRepository supplyRepository;

    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private SugarFactoryRateRepository rateRepository;

    @Override
    public SugarcaneSupply addSupplyEntry(String farmerCode, double tonnes, String tractorNumber, String driverName) {

        // 1. प्रिफिक्स हँडलिंगसह शेतकरी शोधणे
        String cleanedCode = farmerCode;
        if (farmerCode != null && farmerCode.contains("-")) {
            cleanedCode = farmerCode.substring(farmerCode.indexOf("-") + 1);
        }

        final String searchCode = cleanedCode;

        Farmer farmer = farmerRepository.findByFarmerCode(farmerCode)
                .orElseGet(() -> farmerRepository.findByFarmerCode(searchCode)
                        .orElseThrow(() -> new RuntimeException("Farmer not found with code: " + farmerCode)));

        // 2. दर काढणे
        SugarFactoryRate currentRate = rateRepository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new RuntimeException("Sugarcane rate not found in SugarFactoryRate!"));

        double ratePerTon = currentRate.getRateOfSugarcanePerTon().doubleValue();
        double currentTripPrice = tonnes * ratePerTon;

        // 3. त्याच शेतकऱ्याच्या आधीच्या सर्व एन्ट्रीजची बेरीज काढणे (Cumulative)
        List<SugarcaneSupply> previousSupplies = supplyRepository.findByFarmerCode(farmerCode);

        double previousTotalTonnes = previousSupplies.stream().mapToDouble(SugarcaneSupply::getTonnes).sum();
        double previousTotalPrice = previousSupplies.stream().mapToDouble(SugarcaneSupply::getTotalPrice).sum();

        // आजची एन्ट्री मिळवून एकूण (Cumulative) काढणे
        double runningTotalTonnes = previousTotalTonnes + tonnes;
        double runningTotalPrice = previousTotalPrice + currentTripPrice;

        // 4. नवीन पुरवठा रेकॉर्ड ऑब्जेक्ट तयार करणे
        SugarcaneSupply supply = new SugarcaneSupply();
        supply.setFarmerCode(farmerCode);
        supply.setFarmerName(farmer.getFarmerName());
        supply.setSupplyDate(LocalDate.now());
        supply.setTonnes(tonnes);
        supply.setRatePerTon(ratePerTon);
        supply.setTotalPrice(currentTripPrice);

        // 👉 आजवरचा एकूण टन आणि एकूण प्राईस सेट करणे
        supply.setCumulativeTonnes(runningTotalTonnes);
        supply.setCumulativeTotalPrice(runningTotalPrice);

        supply.setTractorNumber(tractorNumber);
        supply.setDriverName(driverName);
        supply.setReceiptNumber("REC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        return supplyRepository.save(supply);
    }

    @Override
    public FarmerSupplySummaryDTO getFarmerSupplySummary(String farmerCode) {
        List<SugarcaneSupply> supplies = supplyRepository.findByFarmerCode(farmerCode);

        if (supplies.isEmpty()) {
            throw new RuntimeException("No sugarcane supplies found for farmer code: " + farmerCode);
        }

        String farmerName = supplies.get(0).getFarmerName();
        double totalTonnes = supplies.stream().mapToDouble(SugarcaneSupply::getTonnes).sum();
        double grandTotalAmount = supplies.stream().mapToDouble(SugarcaneSupply::getTotalPrice).sum();

        return new FarmerSupplySummaryDTO(
                farmerCode,
                farmerName,
                supplies.size(),
                totalTonnes,
                grandTotalAmount,
                supplies
        );
    }

    @Override
    public List<SugarcaneSupply> getAllSupplies() {
        return supplyRepository.findAll();
    }

    @Override
    public byte[] generateSupplyReceiptPdf(Long supplyId) {
        SugarcaneSupply supply = supplyRepository.findById(supplyId)
                .orElseThrow(() -> new RuntimeException("Sugarcane supply record not found with id: " + supplyId));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("SUGAR FACTORY - SUGARCANE WEIGHMENT RECEIPT");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.newLineAtOffset(0, -30);
                contentStream.showText("--------------------------------------------------------------------------------------------------------");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Receipt Number   : " + supply.getReceiptNumber());

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Supply Date      : " + supply.getSupplyDate());

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("--------------------------------------------------------------------------------------------------------");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contentStream.newLineAtOffset(0, -25);
                contentStream.showText("FARMER DETAILS:");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Farmer Code      : " + supply.getFarmerCode());

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Farmer Name      : " + supply.getFarmerName());

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("--------------------------------------------------------------------------------------------------------");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contentStream.newLineAtOffset(0, -25);
                contentStream.showText("TRIP & PRICING DETAILS:");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Tractor Number   : " + supply.getTractorNumber());

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Driver Name      : " + supply.getDriverName());

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Sugarcane Weight : " + supply.getTonnes() + " Tonnes");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Rate Per Ton     : Rs. " + supply.getRatePerTon() + " (From SugarFactoryRate)");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 11);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Total Trip Price : Rs. " + supply.getTotalPrice());

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("--------------------------------------------------------------------------------------------------------");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
                contentStream.newLineAtOffset(0, -30);
                contentStream.showText("This is a system-generated computer receipt.");

                contentStream.endText();
            }

            document.save(out);
        } catch (IOException e) {
            throw new RuntimeException("Error while generating PDF receipt", e);
        }

        return out.toByteArray();
    }

    @Override
    public List<SugarcaneSupply> getSuppliesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<SugarcaneSupply> supplies = supplyRepository.findBySupplyDateBetween(startDate, endDate);

        if (supplies.isEmpty()) {
            throw new RuntimeException("No sugarcane supplies found between " + startDate + " and " + endDate);
        }

        return supplies;
    }
}
package com.example.farmer.canematrix.service.impl;

import com.example.farmer.canematrix.entity.Farmer;
import com.example.farmer.canematrix.entity.FarmerNominee;
import com.example.farmer.canematrix.entity.ShareTransfer;
import com.example.farmer.canematrix.exception.FarmerNotFoundException;     // 👈 शेतकरी सापडला नाही तर
import com.example.farmer.canematrix.exception.ResourceNotFoundException; // 👈 रेकॉर्ड सापडला नाही तर
import com.example.farmer.canematrix.repository.FarmerNomineeRepository;
import com.example.farmer.canematrix.repository.FarmerRepository;
import com.example.farmer.canematrix.repository.ShareTransferRepository;
import com.example.farmer.canematrix.service.ShareTransferService;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ShareTransferServiceImpl implements ShareTransferService {

    @Autowired
    private ShareTransferRepository shareTransferRepository;

    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private FarmerNomineeRepository nomineeRepository;

    // इथे Absolute Path सेट केलाय जेणेकरून FileNotFound एरर येणार नाही
    private final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "documents" + File.separator;

    private String extractOriginalCode(String farmerCode) {
        if (farmerCode == null || farmerCode.trim().isEmpty()) {
            return farmerCode;
        }

        // १. समजा चुकून कॉमा किंवा मल्टिपल कोड्स आले तर पहिला भाग निवडणे
        if (farmerCode.contains(",")) {
            farmerCode = farmerCode.split(",")[0].trim();
        }

        // २. प्रिफिक्स (A-, C-, A_, C_) काढणे
        if (farmerCode.length() > 2) {
            String prefix = farmerCode.substring(0, 2).toUpperCase();
            if (prefix.equals("A-") || prefix.equals("C-") || prefix.equals("A_") || prefix.equals("C_")) {
                farmerCode = farmerCode.substring(2);
            }
        }

        return farmerCode.trim();
    }

    @Override
    public ShareTransfer createTransferRequest(String farmerCode, String transferReason, MultipartFile documentFile) {

        String originalFarmerCode = extractOriginalCode(farmerCode);

        Farmer farmer = farmerRepository.findByFarmerCode(originalFarmerCode)
                .orElseThrow(() -> new FarmerNotFoundException("Farmer not found with code: " + originalFarmerCode));

        String nomineeName = nomineeRepository.findByFarmerId(farmer.getId())
                .map(FarmerNominee::getNomineeName)
                .orElse("Not Available");

        String transfereeId = "TR-" + originalFarmerCode;

        ShareTransfer transfer = new ShareTransfer();
        transfer.setFarmerCode(originalFarmerCode);
        transfer.setFarmerName(farmer.getFarmerName());
        transfer.setTransfereeId(transfereeId);
        transfer.setTransfereeName(nomineeName);
        transfer.setNomineeName(nomineeName);
        transfer.setTransferDate(LocalDate.now());
        transfer.setTransferReason(transferReason);
        transfer.setStatus(ShareTransfer.TransferStatus.PENDING);

        if (documentFile != null && !documentFile.isEmpty()) {
            try {
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                String fileName = UUID.randomUUID().toString() + "_" + documentFile.getOriginalFilename();
                String filePath = UPLOAD_DIR + fileName;

                documentFile.transferTo(new File(filePath));
                transfer.setDocumentPath(filePath);

            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to store document file!", e);
            }
        }

        return shareTransferRepository.save(transfer);
    }

    @Override
    public ShareTransfer updateTransferStatus(Long id, ShareTransfer.TransferStatus status) {
        ShareTransfer transfer = shareTransferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Share Transfer request not found with id: " + id));

        transfer.setStatus(status);
        return shareTransferRepository.save(transfer);
    }

    @Override
    public List<ShareTransfer> getAllTransfers() {
        return shareTransferRepository.findAll();
    }

    @Override
    public List<ShareTransfer> getTransfersByFarmer(String farmerCode) {
        String originalFarmerCode = extractOriginalCode(farmerCode);
        return shareTransferRepository.findByFarmerCode(originalFarmerCode);
    }

    @Override
    public byte[] generateTransferReceiptPdf(Long transferId) {
        ShareTransfer transfer = shareTransferRepository.findById(transferId)
                .orElseThrow(() -> new ResourceNotFoundException("Share Transfer record not found with id: " + transferId));

        if (transfer.getStatus() != ShareTransfer.TransferStatus.APPROVED) {
            throw new IllegalArgumentException("Receipt can only be generated for APPROVED share transfers!");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("SUGAR FACTORY - SHARE TRANSFER CERTIFICATE");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);

                contentStream.newLineAtOffset(0, -30);
                contentStream.showText("--------------------------------------------------------------------------------------------------------");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Transfer ID      : " + transfer.getId());

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Transfer Date    : " + transfer.getTransferDate());

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Transfer Reason  : " + transfer.getTransferReason());

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Status           : " + transfer.getStatus());

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("--------------------------------------------------------------------------------------------------------");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contentStream.newLineAtOffset(0, -25);
                contentStream.showText("PREVIOUS FARMER DETAILS:");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Farmer Code      : " + transfer.getFarmerCode());

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Farmer Name      : " + transfer.getFarmerName());

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("--------------------------------------------------------------------------------------------------------");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contentStream.newLineAtOffset(0, -25);
                contentStream.showText("NEW MEMBER / TRANSFEREE DETAILS:");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("New Member ID    : " + transfer.getTransfereeId());

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("New Member Name  : " + transfer.getTransfereeName());

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Nominee Name     : " + transfer.getNomineeName());

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("--------------------------------------------------------------------------------------------------------");

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
                contentStream.newLineAtOffset(0, -30);
                contentStream.showText("This is a system-generated share transfer certificate.");

                contentStream.endText();
            }

            document.save(out);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error while generating Share Transfer PDF", e);
        }

        return out.toByteArray();
    }
}
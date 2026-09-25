package com.example.farmer.canematrix.controller;

import com.example.farmer.canematrix.entity.ShareAllocation;
import com.example.farmer.canematrix.entity.SugarcaneSupply;
import com.example.farmer.canematrix.repository.ShareAllocationRepository;
import com.example.farmer.canematrix.repository.SugarcaneSupplyRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private SugarcaneSupplyRepository supplyRepository;

    @Autowired
    private ShareAllocationRepository shareAllocationRepository;

    @GetMapping("/sugarcane-excel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLERK')")
    public void downloadSugarcaneExcel(
            @RequestParam(required = false) String farmerCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate supplyDate,
            HttpServletResponse response) throws IOException {

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=Sugarcane_Supply_Report.xlsx");

        List<SugarcaneSupply> supplies = supplyRepository.findAll();

        if (farmerCode != null && !farmerCode.trim().isEmpty()) {
            String searchCode = farmerCode.trim().toLowerCase();
            supplies = supplies.stream()
                    .filter(s -> s.getFarmerCode() != null &&
                            s.getFarmerCode().toLowerCase().contains(searchCode))
                    .collect(Collectors.toList());
        }

        if (supplyDate != null) {
            supplies = supplies.stream()
                    .filter(s -> s.getSupplyDate() != null &&
                            s.getSupplyDate().toLocalDate().equals(supplyDate))
                    .collect(Collectors.toList());
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sugarcane Supply");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Farmer Code", "Farmer Name", "Farm Code", "Planting Date", "Tonnes", "Date & Time", "Tractor No"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (SugarcaneSupply supply : supplies) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(supply.getId());
                row.createCell(1).setCellValue(supply.getFarmerCode() != null ? supply.getFarmerCode() : "");
                row.createCell(2).setCellValue(supply.getFarmerName() != null ? supply.getFarmerName() : "");
                row.createCell(3).setCellValue(supply.getFarmCode() != null ? supply.getFarmCode() : "");
                row.createCell(4).setCellValue(supply.getPlantingDate() != null ? supply.getPlantingDate().toString() : "");
                row.createCell(5).setCellValue(supply.getTonnes());
                row.createCell(6).setCellValue(supply.getSupplyDate() != null ? supply.getSupplyDate().toString().replace("T", " ") : "");
                row.createCell(7).setCellValue(supply.getTractorNumber() != null ? supply.getTractorNumber() : "");
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    @GetMapping("/share-allocation-report-excel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLERK')")
    public void downloadShareAllocationExcel(
            @RequestParam(required = false) String typeOfShare,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletResponse response) throws IOException {

        response.setContentType("application/octet-stream");
        String filename = (typeOfShare != null ? typeOfShare.toUpperCase() : "All") + "_Share_Allocation_Report.xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);

        List<ShareAllocation> allocations = shareAllocationRepository.findAll();

        if (typeOfShare != null && !typeOfShare.trim().isEmpty()) {
            String searchType = typeOfShare.trim().toUpperCase();
            allocations = allocations.stream()
                    .filter(a -> a.getTypeOfShare() != null &&
                            a.getTypeOfShare().equalsIgnoreCase(searchType))
                    .collect(Collectors.toList());
        }

        if (startDate != null && endDate != null) {
            allocations = allocations.stream()
                    .filter(a -> a.getPurchasingDate() != null &&
                            !a.getPurchasingDate().isBefore(startDate) &&
                            !a.getPurchasingDate().isAfter(endDate))
                    .collect(Collectors.toList());
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Share Allocations");

            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 13);
            titleFont.setColor(IndexedColors.DARK_BLUE.getIndex());
            titleStyle.setFont(titleFont);

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            String reportTitle = "SHARE ALLOCATION REPORT - " +
                    (typeOfShare != null && !typeOfShare.trim().isEmpty() ? typeOfShare.toUpperCase() + " TYPE" : "ALL TYPES");
            titleCell.setCellValue(reportTitle);
            titleCell.setCellStyle(titleStyle);

            int rowIdx = 2;

            CellStyle headerStyle = workbook.createCellStyle();
            Font whiteFont = workbook.createFont();
            whiteFont.setBold(true);
            whiteFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(whiteFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] columns = {"ID", "Farmer Code", "Farmer Name", "Share Type", "Purchased Date", "Share Price", "Total Price", "Sugarcane Type", "Planting Date", "Director"};
            Row headerRow = sheet.createRow(rowIdx++);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            BigDecimal totalShareAmountSum = BigDecimal.ZERO;

            for (ShareAllocation alloc : allocations) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(alloc.getId());
                row.createCell(1).setCellValue(alloc.getFarmerCode() != null ? alloc.getFarmerCode() : "");
                row.createCell(2).setCellValue(alloc.getFarmerName() != null ? alloc.getFarmerName() : "");
                row.createCell(3).setCellValue(alloc.getTypeOfShare() != null ? alloc.getTypeOfShare() : "");
                row.createCell(4).setCellValue(alloc.getPurchasingDate() != null ? alloc.getPurchasingDate().toString() : "");
                row.createCell(5).setCellValue(alloc.getSharePrice() != null ? alloc.getSharePrice().doubleValue() : 0.0);
                row.createCell(6).setCellValue(alloc.getTotalPrice() != null ? alloc.getTotalPrice().doubleValue() : 0.0);
                row.createCell(7).setCellValue(alloc.getTypeOfSugarcane() != null ? alloc.getTypeOfSugarcane() : "");
                row.createCell(8).setCellValue(alloc.getPlantingDate() != null ? alloc.getPlantingDate().toString() : "");
                row.createCell(9).setCellValue(alloc.getDirectorName() != null ? alloc.getDirectorName() : "");

                if (alloc.getTotalPrice() != null) {
                    totalShareAmountSum = totalShareAmountSum.add(alloc.getTotalPrice());
                }
            }

            Row totalRow = sheet.createRow(rowIdx);
            totalRow.createCell(4).setCellValue("GRAND TOTAL:");
            totalRow.createCell(6).setCellValue(totalShareAmountSum.doubleValue());

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }
}
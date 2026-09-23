package com.example.farmer.canematrix.dto;

import lombok.Data;

@Data
public class FarmDetailResponse {
    private Long id;
    private String gatNumber;
    private Double totalAreaAcre;
    private Double sugarcaneAreaAcre;
    private String irrigationSource;
    private String village;
}
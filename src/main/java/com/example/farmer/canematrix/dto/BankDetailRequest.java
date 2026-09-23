package com.example.farmer.canematrix.dto;

import lombok.Data;

@Data
public class BankDetailRequest {
    private String bankName;
    private String branchName;
    private String accountNumber;
    private String accountHolderName;
    private String ifscCode;
    private Boolean isPrimary;
}
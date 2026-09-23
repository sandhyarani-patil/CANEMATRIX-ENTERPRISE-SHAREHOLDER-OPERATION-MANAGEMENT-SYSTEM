package com.example.farmer.canematrix.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class NomineeRequest {
    private String nomineeName;
    private String relation;
    private LocalDate dateOfBirth;
    private String mobileNumber;
}
package com.example.farmer.canematrix.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class NomineeResponse {
    private Long id;
    private String nomineeName;
    private String relation;
    private LocalDate dateOfBirth;
    private String mobileNumber;
}
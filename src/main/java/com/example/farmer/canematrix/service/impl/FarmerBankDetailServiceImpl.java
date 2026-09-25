package com.example.farmer.canematrix.service.impl;

import com.example.farmer.canematrix.entity.Farmer;
import com.example.farmer.canematrix.entity.FarmerBankDetail;
import com.example.farmer.canematrix.exception.FarmerNotFoundException;
import com.example.farmer.canematrix.exception.ResourceNotFoundException; // 👈 नवीन एक्सेप्शन इम्पोर्ट केली
import com.example.farmer.canematrix.repository.FarmerBankDetailRepository;
import com.example.farmer.canematrix.repository.FarmerRepository;
import com.example.farmer.canematrix.service.FarmerBankDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service // <-- हे ॲनोटेशन Spring Boot Bean बनवण्यासाठी अत्यंत आवश्यक आहे!
@Transactional
public class FarmerBankDetailServiceImpl implements FarmerBankDetailService {

    private final FarmerBankDetailRepository bankRepository;
    private final FarmerRepository farmerRepository;

    public FarmerBankDetailServiceImpl(FarmerBankDetailRepository bankRepository, FarmerRepository farmerRepository) {
        this.bankRepository = bankRepository;
        this.farmerRepository = farmerRepository;
    }

    @Override
    public FarmerBankDetail addBankDetail(Long farmerId, FarmerBankDetail bankDetail) {
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new FarmerNotFoundException("Farmer not found with id: " + farmerId));
        bankDetail.setFarmer(farmer);
        return bankRepository.save(bankDetail);
    }

    @Override
    @Transactional(readOnly = true)
    public FarmerBankDetail getBankDetailByFarmerId(Long farmerId) {
        return bankRepository.findByFarmerId(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank detail not found for farmer id: " + farmerId));
    }

    @Override
    public FarmerBankDetail updateBankDetail(Long bankId, FarmerBankDetail updatedDetail) {
        FarmerBankDetail existing = bankRepository.findById(bankId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank detail not found with id: " + bankId));

        existing.setAccountHolderName(updatedDetail.getAccountHolderName());
        existing.setAccountNumber(updatedDetail.getAccountNumber());
        existing.setBankName(updatedDetail.getBankName());
        existing.setIfscCode(updatedDetail.getIfscCode());
        existing.setBranchName(updatedDetail.getBranchName());

        return bankRepository.save(existing);
    }

    @Override
    public FarmerBankDetail partialUpdateBankDetail(Long bankId, Map<String, Object> updates) {
        FarmerBankDetail existing = bankRepository.findById(bankId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank detail not found with id: " + bankId));

        updates.forEach((key, value) -> {
            if (value != null) {
                switch (key) {
                    case "accountHolderName" -> existing.setAccountHolderName((String) value);
                    case "accountNumber" -> existing.setAccountNumber((String) value);
                    case "bankName" -> existing.setBankName((String) value);
                    case "ifscCode" -> existing.setIfscCode((String) value);
                    case "branchName" -> existing.setBranchName((String) value);
                }
            }
        });

        return bankRepository.save(existing);
    }
}
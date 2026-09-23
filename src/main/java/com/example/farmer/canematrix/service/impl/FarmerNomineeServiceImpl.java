package com.example.farmer.canematrix.service.impl;

import com.example.farmer.canematrix.entity.Farmer;
import com.example.farmer.canematrix.entity.FarmerNominee;
import com.example.farmer.canematrix.exception.FarmerNotFoundException;
import com.example.farmer.canematrix.repository.FarmerNomineeRepository;
import com.example.farmer.canematrix.repository.FarmerRepository;
import com.example.farmer.canematrix.service.FarmerNomineeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

@Service
@Transactional
public class FarmerNomineeServiceImpl implements FarmerNomineeService {

    private final FarmerNomineeRepository nomineeRepository;
    private final FarmerRepository farmerRepository;

    public FarmerNomineeServiceImpl(FarmerNomineeRepository nomineeRepository, FarmerRepository farmerRepository) {
        this.nomineeRepository = nomineeRepository;
        this.farmerRepository = farmerRepository;
    }

    @Override
    public FarmerNominee addNominee(Long farmerId, FarmerNominee nominee) {
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new FarmerNotFoundException("Farmer not found with id: " + farmerId));

        nominee.setFarmer(farmer);
        return nomineeRepository.save(nominee);
    }

    @Override
    @Transactional(readOnly = true)
    public FarmerNominee getNomineeByFarmerId(Long farmerId) {
        return nomineeRepository.findByFarmerId(farmerId)
                .orElseThrow(() -> new RuntimeException("Nominee details not found for farmer id: " + farmerId));
    }

    @Override
    public FarmerNominee partialUpdateNominee(Long nomineeId, Map<String, Object> updates) {
        FarmerNominee existing = nomineeRepository.findById(nomineeId)
                .orElseThrow(() -> new RuntimeException("Nominee not found with id: " + nomineeId));

        updates.forEach((key, value) -> {
            if (value != null) {
                switch (key) {
                    case "nomineeName" -> existing.setNomineeName((String) value);
                    case "relation" -> existing.setRelation((String) value);
                    case "dateOfBirth" -> existing.setDateOfBirth(LocalDate.parse((String) value));
                    case "mobileNumber" -> existing.setMobileNumber((String) value);
                }
            }
        });

        return nomineeRepository.save(existing);
    }
}
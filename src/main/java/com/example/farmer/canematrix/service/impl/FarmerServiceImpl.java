package com.example.farmer.canematrix.service.impl;

import com.example.farmer.canematrix.dto.FarmerRequest;
import com.example.farmer.canematrix.dto.BankDetailResponse;
import com.example.farmer.canematrix.dto.FarmDetailResponse;
import com.example.farmer.canematrix.dto.FarmerResponse;
import com.example.farmer.canematrix.dto.NomineeResponse;
import com.example.farmer.canematrix.entity.Farmer;
import com.example.farmer.canematrix.entity.FarmerBankDetail;
import com.example.farmer.canematrix.entity.FarmerFarmDetail;
import com.example.farmer.canematrix.entity.FarmerNominee;
import com.example.farmer.canematrix.entity.FarmerStatus;
import com.example.farmer.canematrix.exception.FarmerNotFoundException;
import com.example.farmer.canematrix.repository.FarmerRepository;
import com.example.farmer.canematrix.service.FarmerService;
import com.example.farmer.canematrix.service.SmsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class FarmerServiceImpl implements FarmerService {

    private final FarmerRepository farmerRepository;

    @Autowired(required = false)
    private SmsService smsService;

    // 👉 पासवर्ड एन्कोडर
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public FarmerServiceImpl(FarmerRepository farmerRepository) {
        this.farmerRepository = farmerRepository;
    }

    // 1. CREATE FARMER
    @Override
    public FarmerResponse createFarmer(FarmerRequest request) {

        if (farmerRepository.existsByFarmerCode(request.getFarmerCode())) {
            throw new IllegalArgumentException("Farmer code already exists");
        }

        if (farmerRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new IllegalArgumentException("Mobile number already exists");
        }

        Farmer farmer = mapRequestToEntity(request, new Farmer());

        // 👉 पासवर्ड एन्कोडिंग लॉजिक
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            farmer.setPassword(passwordEncoder.encode(request.getPassword()));
        } else {
            farmer.setPassword(passwordEncoder.encode("Farmer@123")); // डीफॉल्ट पासवर्ड
        }

        farmer.setRole("ROLE_FARMER");

        if (farmer.getStatus() == null) {
            farmer.setStatus(FarmerStatus.ACTIVE);
        }

        Farmer savedFarmer = farmerRepository.save(farmer);

        sendRegistrationEligibilityNotification(savedFarmer);

        return convertToResponse(savedFarmer);
    }

    // 2. GET ALL FARMERS
    @Override
    @Transactional(readOnly = true)
    public List<FarmerResponse> getAllFarmers() {
        return farmerRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // 3. GET FARMER BY ID
    @Override
    @Transactional(readOnly = true)
    public FarmerResponse getFarmerById(Long id) {
        Farmer farmer = farmerRepository.findById(id)
                .orElseThrow(() -> new FarmerNotFoundException("Farmer not found with id: " + id));
        return convertToResponse(farmer);
    }

    // 4. UPDATE FARMER
    @Override
    public FarmerResponse updateFarmer(Long id, FarmerRequest request) {
        Farmer farmer = farmerRepository.findById(id)
                .orElseThrow(() -> new FarmerNotFoundException("Farmer not found with id: " + id));

        farmer = mapRequestToEntity(request, farmer);
        farmer.setUpdatedAt(LocalDateTime.now());
        Farmer updatedFarmer = farmerRepository.save(farmer);

        return convertToResponse(updatedFarmer);
    }

    // 5. DELETE FARMER
    @Override
    public void deleteFarmer(Long id) {
        Farmer farmer = farmerRepository.findById(id)
                .orElseThrow(() -> new FarmerNotFoundException("Farmer not found with id: " + id));
        farmerRepository.delete(farmer);
    }

    private void sendRegistrationEligibilityNotification(Farmer farmer) {
        if (smsService != null && farmer.getMobileNumber() != null && !farmer.getMobileNumber().isBlank()) {
            String message = """
                    नमस्कार %s,
                    CaneMatrix मध्ये तुमची नोंदणी यशस्वीरीत्या पूर्ण झाली आहे.
                    तुम्ही शेअर वाटप प्रक्रियेसाठी (Share Allocation) पात्र आहात.
                    - CaneMatrix Team""".formatted(farmer.getFarmerName());

            smsService.sendSms(farmer.getMobileNumber(), message);
        }
    }

    private Farmer mapRequestToEntity(FarmerRequest request, Farmer farmer) {
        if (request.getFarmerCode() != null) farmer.setFarmerCode(request.getFarmerCode());
        if (request.getFarmerName() != null) farmer.setFarmerName(request.getFarmerName());

        // 👉 पासवर्ड अपडेट असल्यास एन्कोड करणे
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            farmer.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getGender() != null) farmer.setGender(request.getGender());
        if (request.getDateOfBirth() != null) farmer.setDateOfBirth(request.getDateOfBirth());
        if (request.getMobileNumber() != null) farmer.setMobileNumber(request.getMobileNumber());
        if (request.getAlternateMobileNumber() != null) farmer.setAlternateMobileNumber(request.getAlternateMobileNumber());
        if (request.getEmail() != null) farmer.setEmail(request.getEmail());
        if (request.getAddress() != null) farmer.setAddress(request.getAddress());
        if (request.getVillage() != null) farmer.setVillage(request.getVillage());
        if (request.getTaluka() != null) farmer.setTaluka(request.getTaluka());
        if (request.getDistrict() != null) farmer.setDistrict(request.getDistrict());
        if (request.getState() != null) farmer.setState(request.getState());
        if (request.getPincode() != null) farmer.setPincode(request.getPincode());
        if (request.getPanNumber() != null) farmer.setPanNumber(request.getPanNumber());
        if (request.getAadharNumber() != null) farmer.setAadharNumber(request.getAadharNumber());
        if (request.getRegistrationDate() != null) farmer.setRegistrationDate(request.getRegistrationDate());

        if (request.getHas712() != null) farmer.setHas712(request.getHas712());
        if (request.getHas8A() != null) farmer.setHas8A(request.getHas8A());
        if (request.getFarmArea() != null) farmer.setFarmArea(request.getFarmArea());

        if (request.getBankDetail() != null) {
            FarmerBankDetail bank = farmer.getBankDetail() != null ? farmer.getBankDetail() : new FarmerBankDetail();
            bank.setBankName(request.getBankDetail().getBankName());
            bank.setBranchName(request.getBankDetail().getBranchName());
            bank.setAccountNumber(request.getBankDetail().getAccountNumber());
            bank.setAccountHolderName(request.getBankDetail().getAccountHolderName());
            bank.setIfscCode(request.getBankDetail().getIfscCode());
            bank.setIsPrimary(request.getBankDetail().getIsPrimary());
            bank.setFarmer(farmer);
            farmer.setBankDetail(bank);
        }

        if (request.getNominee() != null) {
            FarmerNominee nominee = farmer.getNominee() != null ? farmer.getNominee() : new FarmerNominee();
            nominee.setNomineeName(request.getNominee().getNomineeName());
            nominee.setRelation(request.getNominee().getRelation());
            nominee.setDateOfBirth(request.getNominee().getDateOfBirth());
            nominee.setMobileNumber(request.getNominee().getMobileNumber());
            nominee.setFarmer(farmer);
            farmer.setNominee(nominee);
        }

        if (request.getFarmDetails() != null && !request.getFarmDetails().isEmpty()) {
            List<FarmerFarmDetail> farmList = request.getFarmDetails().stream().map(fDto -> {
                FarmerFarmDetail farm = new FarmerFarmDetail();
                farm.setGatNumber(fDto.getGatNumber());
                farm.setTotalAreaAcre(fDto.getTotalAreaAcre());
                farm.setSugarcaneAreaAcre(fDto.getSugarcaneAreaAcre());
                farm.setIrrigationSource(fDto.getIrrigationSource());
                farm.setVillage(fDto.getVillage());
                farm.setFarmer(farmer);
                return farm;
            }).toList();

            farmer.getFarmDetails().clear();
            farmer.getFarmDetails().addAll(farmList);
        }

        return farmer;
    }

    private FarmerResponse convertToResponse(Farmer farmer) {
        FarmerResponse response = new FarmerResponse();
        response.setId(farmer.getId());
        response.setFarmerCode(farmer.getFarmerCode());
        response.setFarmerName(farmer.getFarmerName());
        response.setGender(farmer.getGender());
        response.setDateOfBirth(farmer.getDateOfBirth());
        response.setMobileNumber(farmer.getMobileNumber());
        response.setAlternateMobileNumber(farmer.getAlternateMobileNumber());
        response.setEmail(farmer.getEmail());
        response.setAddress(farmer.getAddress());
        response.setVillage(farmer.getVillage());
        response.setTaluka(farmer.getTaluka());
        response.setDistrict(farmer.getDistrict());
        response.setState(farmer.getState());
        response.setPincode(farmer.getPincode());
        response.setPanNumber(farmer.getPanNumber());
        response.setAadharNumber(farmer.getAadharNumber());
        response.setRegistrationDate(farmer.getRegistrationDate());
        response.setStatus(farmer.getStatus());
        response.setCreatedAt(farmer.getCreatedAt());
        response.setUpdatedAt(farmer.getUpdatedAt());

        response.setHas712(farmer.getHas712());
        response.setHas8A(farmer.getHas8A());
        response.setFarmArea(farmer.getFarmArea());

        if (farmer.getBankDetail() != null) {
            response.setBankDetail(mapBankToResponse(farmer.getBankDetail()));
        }

        if (farmer.getNominee() != null) {
            response.setNominee(mapNomineeToResponse(farmer.getNominee()));
        }

        if (farmer.getFarmDetails() != null && !farmer.getFarmDetails().isEmpty()) {
            List<FarmDetailResponse> farmRespList = farmer.getFarmDetails().stream()
                    .map(this::mapFarmToResponse)
                    .toList();
            response.setFarmDetails(farmRespList);
        }

        return response;
    }

    private BankDetailResponse mapBankToResponse(FarmerBankDetail bank) {
        BankDetailResponse bankResp = new BankDetailResponse();
        bankResp.setId(bank.getId());
        bankResp.setBankName(bank.getBankName());
        bankResp.setBranchName(bank.getBranchName());
        bankResp.setAccountNumber(bank.getAccountNumber());
        bankResp.setAccountHolderName(bank.getAccountHolderName());
        bankResp.setIfscCode(bank.getIfscCode());
        bankResp.setIsPrimary(bank.getIsPrimary());
        return bankResp;
    }

    private NomineeResponse mapNomineeToResponse(FarmerNominee nominee) {
        NomineeResponse nomineeResp = new NomineeResponse();
        nomineeResp.setId(nominee.getId());
        nomineeResp.setNomineeName(nominee.getNomineeName());
        nomineeResp.setRelation(nominee.getRelation());
        nomineeResp.setDateOfBirth(nominee.getDateOfBirth());
        nomineeResp.setMobileNumber(nominee.getMobileNumber());
        return nomineeResp;
    }

    private FarmDetailResponse mapFarmToResponse(FarmerFarmDetail farm) {
        FarmDetailResponse fResp = new FarmDetailResponse();
        fResp.setId(farm.getId());
        fResp.setGatNumber(farm.getGatNumber());
        fResp.setTotalAreaAcre(farm.getTotalAreaAcre());
        fResp.setSugarcaneAreaAcre(farm.getSugarcaneAreaAcre());
        fResp.setIrrigationSource(farm.getIrrigationSource());
        fResp.setVillage(farm.getVillage());
        return fResp;
    }
}
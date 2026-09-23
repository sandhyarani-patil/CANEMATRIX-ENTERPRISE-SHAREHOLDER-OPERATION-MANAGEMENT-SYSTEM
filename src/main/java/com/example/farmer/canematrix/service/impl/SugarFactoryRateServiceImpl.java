package com.example.farmer.canematrix.service.impl;

import com.example.farmer.canematrix.entity.SugarFactoryRate;
import com.example.farmer.canematrix.repository.SugarFactoryRateRepository;
import com.example.farmer.canematrix.service.SugarFactoryRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SugarFactoryRateServiceImpl implements SugarFactoryRateService {

    @Autowired
    private SugarFactoryRateRepository repository;

    @Override
    public SugarFactoryRate saveOrUpdateRate(SugarFactoryRate rate) {
        // १. ऑटोमॅटिकली सध्याची तारीख सेट होईल
        rate.setUpdatedDate(LocalDate.now());

        // २. जर अपडेट होत असेल आणि ID अस्तित्वात असेल, तर जुन्या डेटाची पडताळणी
        if (rate.getId() != null) {
            SugarFactoryRate existingRate = repository.findById(rate.getId())
                    .orElseThrow(() -> new RuntimeException("Sugar Factory Rate not found with ID: " + rate.getId()));

            existingRate.setSugarFactoryName(rate.getSugarFactoryName());
            existingRate.setSharePurchaseAmount(rate.getSharePurchaseAmount());
            existingRate.setPerMonthShareSugar(rate.getPerMonthShareSugar());
            existingRate.setRateOfShareSugar(rate.getRateOfShareSugar());
            existingRate.setRateOfSugarcaneSugar(rate.getRateOfSugarcaneSugar());
            existingRate.setRateOfSugarcanePerTon(rate.getRateOfSugarcanePerTon());
            existingRate.setUpdatedDate(LocalDate.now());

            return repository.save(existingRate);
        }

        return repository.save(rate);
    }

    @Override
    public SugarFactoryRate getLatestRate() {
        return repository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new RuntimeException("No factory rates configured in the system!"));
    }

    @Override
    public List<SugarFactoryRate> getAllRates() {
        return repository.findAll();
    }
}
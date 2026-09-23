package com.example.farmer.canematrix.service;

import com.example.farmer.canematrix.entity.SugarFactoryRate;
import java.util.List;

public interface SugarFactoryRateService {
    SugarFactoryRate saveOrUpdateRate(SugarFactoryRate rate);
    SugarFactoryRate getLatestRate();
    List<SugarFactoryRate> getAllRates();
}
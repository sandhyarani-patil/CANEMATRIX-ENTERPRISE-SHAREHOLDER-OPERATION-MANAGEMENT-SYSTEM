package com.example.farmer.canematrix.service;

import com.example.farmer.canematrix.dto.FestivalSugarMasterDto;
import java.util.List;

public interface FestivalSugarMasterService {
    FestivalSugarMasterDto createFestivalSugar(FestivalSugarMasterDto dto);
    List<FestivalSugarMasterDto> getAllFestivalSugars();
    FestivalSugarMasterDto getFestivalSugarById(Long id);
    FestivalSugarMasterDto updateFestivalSugar(Long id, FestivalSugarMasterDto dto);
    void deleteFestivalSugar(Long id);
}
package com.example.farmer.canematrix.controller;

import com.example.farmer.canematrix.dto.FestivalSugarMasterDto;
import com.example.farmer.canematrix.service.FestivalSugarMasterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/festival-sugar")
public class FestivalSugarMasterController {

    @Autowired
    private FestivalSugarMasterService service;

    // Create Festival Sugar Rule
    @PostMapping
    public ResponseEntity<FestivalSugarMasterDto> createFestivalSugar(@Valid @RequestBody FestivalSugarMasterDto dto) {
        FestivalSugarMasterDto created = service.createFestivalSugar(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Get All Records
    @GetMapping
    public ResponseEntity<List<FestivalSugarMasterDto>> getAllFestivalSugars() {
        return ResponseEntity.ok(service.getAllFestivalSugars());
    }

    // Get By ID
    @GetMapping("/{id}")
    public ResponseEntity<FestivalSugarMasterDto> getFestivalSugarById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getFestivalSugarById(id));
    }

    // Update Record
    @PutMapping("/{id}")
    public ResponseEntity<FestivalSugarMasterDto> updateFestivalSugar(
            @PathVariable Long id,
            @Valid @RequestBody FestivalSugarMasterDto dto) {
        return ResponseEntity.ok(service.updateFestivalSugar(id, dto));
    }

    // Delete Record
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFestivalSugar(@PathVariable Long id) {
        service.deleteFestivalSugar(id);
        return ResponseEntity.ok("Festival Sugar record deleted successfully with ID: " + id);
    }
}
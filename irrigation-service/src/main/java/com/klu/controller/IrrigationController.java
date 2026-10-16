package com.klu.controller;

import com.klu.entity.Irrigation;
import com.klu.service.IrrigationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/irrigation")
public class IrrigationController {

    private final IrrigationService irrigationService;

    public IrrigationController(IrrigationService irrigationService) {
        this.irrigationService = irrigationService;
    }

    @PostMapping
    public ResponseEntity<Irrigation> createIrrigation(
            @RequestBody Irrigation irrigation) {

        return ResponseEntity.ok(
                irrigationService.createIrrigation(irrigation)
        );
    }

    @GetMapping
    public ResponseEntity<List<Irrigation>> getAllIrrigation() {

        return ResponseEntity.ok(
                irrigationService.getAllIrrigation()
        );
    }

    @GetMapping("/field/{fieldId}")
    public ResponseEntity<Irrigation> getByFieldId(
            @PathVariable Long fieldId) {

        return irrigationService
                .getByFieldId(fieldId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/field/{fieldId}/pump")
    public ResponseEntity<Irrigation> updatePumpStatus(
            @PathVariable Long fieldId,
            @RequestParam boolean status) {

        return ResponseEntity.ok(
                irrigationService.updatePumpStatus(fieldId, status)
        );
    }
    @PutMapping("/field/{fieldId}/mode")
    public ResponseEntity<Irrigation> updateMode(
            @PathVariable Long fieldId,
            @RequestParam String mode) {

        return ResponseEntity.ok(
                irrigationService.updateMode(fieldId, mode)
        );
    }
    @PostMapping("/field/{fieldId}/auto")
    public ResponseEntity<Irrigation> automaticIrrigation(
            @PathVariable Long fieldId) {

        return ResponseEntity.ok(
                irrigationService.automaticIrrigation(fieldId)
        );
    }
}
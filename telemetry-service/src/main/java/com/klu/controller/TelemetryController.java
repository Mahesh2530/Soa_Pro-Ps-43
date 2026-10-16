package com.klu.controller;

import com.klu.entity.Telemetry;
import com.klu.service.TelemetryService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/telemetry")
public class TelemetryController {

    private final TelemetryService telemetryService;

    public TelemetryController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @PostMapping
    public ResponseEntity<Telemetry> saveTelemetry(
            @RequestBody Telemetry telemetry) {

        return ResponseEntity.ok(
                telemetryService.saveTelemetry(telemetry)
        );
    }

    @GetMapping("/field/{fieldId}")
    public ResponseEntity<List<Telemetry>> getByFieldId(
            @PathVariable Long fieldId) {

        return ResponseEntity.ok(
                telemetryService.getByFieldId(fieldId)
        );
    }

    @GetMapping("/latest/{fieldId}")
    public ResponseEntity<Telemetry> getLatestByFieldId(
            @PathVariable Long fieldId) {

        return telemetryService
                .getLatestByFieldId(fieldId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
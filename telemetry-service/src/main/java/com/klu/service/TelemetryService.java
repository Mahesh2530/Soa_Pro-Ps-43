package com.klu.service;

import com.klu.entity.Telemetry;
import com.klu.repository.TelemetryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TelemetryService {

    private final TelemetryRepository telemetryRepository;

    public TelemetryService(TelemetryRepository telemetryRepository) {
        this.telemetryRepository = telemetryRepository;
    }

    public Telemetry saveTelemetry(Telemetry telemetry) {

        if (telemetry.getRecordedAt() == null) {
            telemetry.setRecordedAt(LocalDateTime.now());
        }

        return telemetryRepository.save(telemetry);
    }

    public List<Telemetry> getByFieldId(Long fieldId) {
        return telemetryRepository.findByFieldId(fieldId);
    }

    public Optional<Telemetry> getLatestByFieldId(Long fieldId) {
        return telemetryRepository.findTopByFieldIdOrderByRecordedAtDesc(fieldId);
    }
}
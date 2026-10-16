package com.klu.repository;

import com.klu.entity.Telemetry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TelemetryRepository extends JpaRepository<Telemetry, Long> {

    List<Telemetry> findByFieldId(Long fieldId);

    Optional<Telemetry> findTopByFieldIdOrderByRecordedAtDesc(Long fieldId);
}
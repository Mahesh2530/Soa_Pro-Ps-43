package com.klu.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "irrigation")
public class Irrigation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long irrigationId;

    private Long fieldId;

    private boolean pumpStatus;

    private Double moistureThreshold;

    private String mode;

    private LocalDateTime lastUpdated;

    public Irrigation() {
    }

    public Long getIrrigationId() {
        return irrigationId;
    }

    public void setIrrigationId(Long irrigationId) {
        this.irrigationId = irrigationId;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public boolean isPumpStatus() {
        return pumpStatus;
    }

    public void setPumpStatus(boolean pumpStatus) {
        this.pumpStatus = pumpStatus;
    }

    public Double getMoistureThreshold() {
        return moistureThreshold;
    }

    public void setMoistureThreshold(Double moistureThreshold) {
        this.moistureThreshold = moistureThreshold;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
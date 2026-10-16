package com.klu.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "fields")
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fieldId;

    @Column(nullable = false)
    private String fieldName;

    @Column(nullable = false)
    private String crop;

    private Double area;

    private Double moistureThreshold;

    private Double targetMoisture;


    public Field() {
    }


    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }


    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }


    public String getCrop() {
        return crop;
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }


    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }


    public Double getMoistureThreshold() {
        return moistureThreshold;
    }

    public void setMoistureThreshold(Double moistureThreshold) {
        this.moistureThreshold = moistureThreshold;
    }


    public Double getTargetMoisture() {
        return targetMoisture;
    }

    public void setTargetMoisture(Double targetMoisture) {
        this.targetMoisture = targetMoisture;
    }
}
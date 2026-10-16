package com.klu.service;

import com.klu.entity.Field;
import com.klu.repository.FieldRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FieldService {

    private final FieldRepository repository;


    public FieldService(FieldRepository repository) {
        this.repository = repository;
    }


    public List<Field> getAllFields() {

        return repository.findAll();

    }


    public Field getFieldById(Long id) {

        return repository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Field not found")
                );

    }


    public Field createField(Field field) {

        return repository.save(field);

    }


    public Field updateField(Long id, Field updatedField) {

        Field field = getFieldById(id);

        field.setFieldName(updatedField.getFieldName());

        field.setCrop(updatedField.getCrop());

        field.setArea(updatedField.getArea());

        field.setMoistureThreshold(
                updatedField.getMoistureThreshold()
        );

        field.setTargetMoisture(
                updatedField.getTargetMoisture()
        );

        return repository.save(field);
    }


    public void deleteField(Long id) {

        repository.deleteById(id);

    }
}
package com.klu.controller;

import com.klu.entity.Field;
import com.klu.service.FieldService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fields")
public class FieldController {

    private final FieldService service;


    public FieldController(FieldService service) {
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<List<Field>> getAllFields() {

        return ResponseEntity.ok(
                service.getAllFields()
        );

    }


    @GetMapping("/{id}")
    public ResponseEntity<Field> getField(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                service.getFieldById(id)
        );

    }


    @PostMapping
    public ResponseEntity<Field> createField(
            @RequestBody Field field
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        service.createField(field)
                );

    }


    @PutMapping("/{id}")
    public ResponseEntity<Field> updateField(
            @PathVariable Long id,
            @RequestBody Field field
    ) {

        return ResponseEntity.ok(
                service.updateField(id, field)
        );

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteField(
            @PathVariable Long id
    ) {

        service.deleteField(id);

        return ResponseEntity.noContent().build();

    }
}
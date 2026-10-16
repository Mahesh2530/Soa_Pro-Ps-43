package com.klu.repository;

import com.klu.entity.Irrigation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IrrigationRepository extends JpaRepository<Irrigation, Long> {

    Optional<Irrigation> findByFieldId(Long fieldId);
}
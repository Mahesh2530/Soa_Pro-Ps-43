package com.klu.service;

import com.klu.entity.Irrigation;
import com.klu.repository.IrrigationRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class IrrigationService {

    private final IrrigationRepository irrigationRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public IrrigationService(IrrigationRepository irrigationRepository) {
        this.irrigationRepository = irrigationRepository;
    }

    // ----------------------------------------------------
    // CREATE IRRIGATION CONFIGURATION
    // ----------------------------------------------------
    public Irrigation updateMode(Long fieldId, String mode) {

        Irrigation irrigation = irrigationRepository
                .findByFieldId(fieldId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Irrigation configuration not found for field "
                                        + fieldId
                        )
                );

        if (!mode.equalsIgnoreCase("AUTO")
                && !mode.equalsIgnoreCase("MANUAL")) {

            throw new RuntimeException(
                    "Mode must be AUTO or MANUAL"
            );
        }

        irrigation.setMode(mode.toUpperCase());

        /*
         * When switching to AUTO, immediately evaluate
         * the current soil moisture.
         */
        if ("AUTO".equalsIgnoreCase(mode)) {
            try {
                return automaticIrrigation(fieldId);
            } catch (Exception e) {
                irrigation.setLastUpdated(LocalDateTime.now());
                return irrigationRepository.save(irrigation);
            }
        }

        irrigation.setLastUpdated(LocalDateTime.now());

        return irrigationRepository.save(irrigation);
    }
    public Irrigation createIrrigation(Irrigation irrigation) {

        // Default moisture threshold
        if (irrigation.getMoistureThreshold() == null) {
            irrigation.setMoistureThreshold(35.0);
        }

        // Default mode
        if (irrigation.getMode() == null) {
            irrigation.setMode("AUTO");
        }

        // Default pump status
        irrigation.setPumpStatus(false);

        // Set current time
        irrigation.setLastUpdated(LocalDateTime.now());

        return irrigationRepository.save(irrigation);
    }

    // ----------------------------------------------------
    // GET ALL IRRIGATION CONFIGURATIONS
    // ----------------------------------------------------

    public List<Irrigation> getAllIrrigation() {

        return irrigationRepository.findAll();
    }

    // ----------------------------------------------------
    // GET IRRIGATION BY FIELD ID
    // ----------------------------------------------------

    public Optional<Irrigation> getByFieldId(Long fieldId) {

        return irrigationRepository.findByFieldId(fieldId);
    }

    // ----------------------------------------------------
    // MANUAL PUMP CONTROL
    // ----------------------------------------------------

    public Irrigation updatePumpStatus(
            Long fieldId,
            boolean pumpStatus) {

        Irrigation irrigation =
                irrigationRepository
                        .findByFieldId(fieldId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Irrigation configuration not found for field "
                                                + fieldId
                                )
                        );

        irrigation.setPumpStatus(pumpStatus);
        irrigation.setLastUpdated(LocalDateTime.now());

        return irrigationRepository.save(irrigation);
    }

    // ----------------------------------------------------
    // AUTOMATIC IRRIGATION
    // ----------------------------------------------------

    public Irrigation automaticIrrigation(Long fieldId) {

        // Get irrigation configuration
        Irrigation irrigation =
                irrigationRepository
                        .findByFieldId(fieldId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Irrigation configuration not found for field "
                                                + fieldId
                                )
                        );

        // Check whether AUTO mode is enabled
        if (!"AUTO".equalsIgnoreCase(irrigation.getMode())) {

            throw new RuntimeException(
                    "Automatic irrigation is disabled for field "
                            + fieldId
            );
        }

        // Telemetry Service URL
        String telemetryUrl =
                "http://localhost:8082/api/telemetry/latest/"
                        + fieldId;

        // Get latest telemetry
        Map<?, ?> telemetry =
                restTemplate.getForObject(
                        telemetryUrl,
                        Map.class
                );

        // Validate telemetry
        if (telemetry == null ||
                telemetry.get("soilMoisture") == null) {

            throw new RuntimeException(
                    "No telemetry data available for field "
                            + fieldId
            );
        }

        // Read soil moisture
        double soilMoisture =
                ((Number) telemetry.get("soilMoisture"))
                        .doubleValue();

        // Get threshold
        double threshold =
                irrigation.getMoistureThreshold();

        // Decide pump status
        boolean pumpShouldBeOn =
                soilMoisture < threshold;

        // Update pump
        irrigation.setPumpStatus(pumpShouldBeOn);

        // Update timestamp
        irrigation.setLastUpdated(
                LocalDateTime.now()
        );

        // Save decision
        Irrigation saved =
                irrigationRepository.save(irrigation);

        // Console information
        System.out.println(
                "----------------------------------------"
        );

        System.out.println(
                "Farmora Automatic Irrigation"
        );

        System.out.println(
                "Field ID       : " + fieldId
        );

        System.out.println(
                "Soil Moisture  : " + soilMoisture + "%"
        );

        System.out.println(
                "Threshold      : " + threshold + "%"
        );

        System.out.println(
                "Pump Status    : "
                        + (pumpShouldBeOn ? "ON" : "OFF")
        );

        System.out.println(
                "----------------------------------------"
        );

        return saved;
    }

    // ----------------------------------------------------
    // AUTOMATIC SCHEDULER
    // ----------------------------------------------------
    //
    // Runs every 30 seconds
    //

    @Scheduled(fixedRate = 5000)
    public void runAutomaticIrrigation() {

        List<Irrigation> irrigationList =
                irrigationRepository.findAll();

        for (Irrigation irrigation : irrigationList) {

            // Only AUTO mode fields are processed
            if ("AUTO".equalsIgnoreCase(
                    irrigation.getMode())) {

                try {

                    automaticIrrigation(
                            irrigation.getFieldId()
                    );

                    System.out.println(
                            "Automatic irrigation checked for field "
                                    + irrigation.getFieldId()
                    );

                } catch (Exception e) {

                    System.out.println(
                            "Irrigation check failed for field "
                                    + irrigation.getFieldId()
                                    + " : "
                                    + e.getMessage()
                    );
                }
            }
        }
    }
}
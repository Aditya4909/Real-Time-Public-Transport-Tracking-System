package com.transittrack.controller;

import com.transittrack.dto.request.LocationUpdateRequest;
import com.transittrack.dto.response.VehicleLocationResponse;
import com.transittrack.service.TrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * Controller handling real-time WebSocket STOMP incoming messages from drivers and onboard GPS hardware.
 *
 * Annotations explanation:
 * - @Controller: Marks the class as a Spring MVC / WebSocket messaging controller.
 * - @MessageMapping("/location.update"): Maps messages sent to STOMP destination '/app/location.update' to this method.
 * - @Payload: Extracts and deserializes the incoming JSON body to LocationUpdateRequest.
 */
@Controller
public class WebSocketTrackingController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketTrackingController.class);

    private final TrackingService trackingService;

    public WebSocketTrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    /**
     * Handles incoming GPS telemetry from drivers sent via WebSocket channel /app/location.update.
     */
    @MessageMapping("/location.update")
    public void handleLocationUpdate(@Payload LocationUpdateRequest request) {
        logger.debug("Received WebSocket telemetry for vehicle ID: {}, lat: {}, lon: {}",
                request.getVehicleId(), request.getLatitude(), request.getLongitude());

        VehicleLocationResponse response = trackingService.processLocationUpdate(request);
        logger.debug("Successfully processed and broadcasted location for vehicle: {}", response.getRegistrationNumber());
    }
}

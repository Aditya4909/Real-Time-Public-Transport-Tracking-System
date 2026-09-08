package com.transittrack.service.impl;

import com.transittrack.dto.request.LocationUpdateRequest;
import com.transittrack.dto.response.PagedResponse;
import com.transittrack.dto.response.VehicleLocationResponse;
import com.transittrack.entity.Vehicle;
import com.transittrack.entity.VehicleLocationLog;
import com.transittrack.entity.VehicleStatus;
import com.transittrack.exception.ResourceNotFoundException;
import com.transittrack.repository.VehicleLocationLogRepository;
import com.transittrack.repository.VehicleRepository;
import com.transittrack.service.TrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation of TrackingService responsible for telemetry ingestion, database persistence,
 * and broadcasting live coordinates over STOMP WebSocket topics using SimpMessagingTemplate.
 *
 * Annotations explanation:
 * - @Service: Declares this class as a Spring service bean.
 * - SimpMessagingTemplate: Spring's high-level messaging abstraction for sending messages to STOMP message broker destinations.
 */
@Service
public class TrackingServiceImpl implements TrackingService {

    private static final Logger logger = LoggerFactory.getLogger(TrackingServiceImpl.class);

    private final VehicleRepository vehicleRepository;
    private final VehicleLocationLogRepository locationLogRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public TrackingServiceImpl(VehicleRepository vehicleRepository,
                               VehicleLocationLogRepository locationLogRepository,
                               SimpMessagingTemplate messagingTemplate) {
        this.vehicleRepository = vehicleRepository;
        this.locationLogRepository = locationLogRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    @Transactional
    public VehicleLocationResponse processLocationUpdate(LocationUpdateRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", request.getVehicleId()));

        LocalDateTime now = LocalDateTime.now();

        // Update latest vehicle location & status
        vehicle.setCurrentLatitude(request.getLatitude());
        vehicle.setCurrentLongitude(request.getLongitude());
        vehicle.setCurrentSpeedKmh(request.getSpeedKmh());
        vehicle.setHeading(request.getHeading());
        vehicle.setLastLocationUpdate(now);

        // If vehicle was INACTIVE, transition to ACTIVE upon receiving live GPS ping
        if (vehicle.getStatus() == VehicleStatus.INACTIVE) {
            vehicle.setStatus(VehicleStatus.ACTIVE);
        }

        vehicleRepository.save(vehicle);

        // Record entry in time-series telemetry log
        VehicleLocationLog log = new VehicleLocationLog(
                vehicle,
                request.getLatitude(),
                request.getLongitude(),
                request.getSpeedKmh(),
                request.getHeading(),
                now
        );
        locationLogRepository.save(log);

        Long routeId = vehicle.getRoute() != null ? vehicle.getRoute().getId() : null;
        String routeNumber = vehicle.getRoute() != null ? vehicle.getRoute().getRouteNumber() : null;

        VehicleLocationResponse response = new VehicleLocationResponse(
                vehicle.getId(),
                vehicle.getRegistrationNumber(),
                routeId,
                routeNumber,
                vehicle.getCurrentLatitude(),
                vehicle.getCurrentLongitude(),
                vehicle.getCurrentSpeedKmh(),
                vehicle.getHeading(),
                now
        );

        // Broadcast to WebSocket subscribers
        // Topic 1: Specific vehicle subscribers: /topic/vehicles/{vehicleId}/location
        String vehicleTopic = "/topic/vehicles/" + vehicle.getId() + "/location";
        messagingTemplate.convertAndSend(vehicleTopic, response);
        logger.debug("Broadcasted location update to STOMP destination: {}", vehicleTopic);

        // Topic 2: Route subscribers: /topic/routes/{routeId}/locations
        if (routeId != null) {
            String routeTopic = "/topic/routes/" + routeId + "/locations";
            messagingTemplate.convertAndSend(routeTopic, response);
            logger.debug("Broadcasted location update to route STOMP destination: {}", routeTopic);
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<VehicleLocationLog> getVehicleLocationHistory(Long vehicleId, int page, int size) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new ResourceNotFoundException("Vehicle", "id", vehicleId);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleLocationLog> logs = locationLogRepository.findByVehicleIdOrderByRecordedAtDesc(vehicleId, pageable);

        return new PagedResponse<>(logs.getContent(), logs.getNumber(), logs.getSize(), logs.getTotalElements(), logs.getTotalPages(), logs.isLast());
    }
}

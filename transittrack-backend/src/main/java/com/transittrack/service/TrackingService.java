package com.transittrack.service;

import com.transittrack.dto.request.LocationUpdateRequest;
import com.transittrack.dto.response.PagedResponse;
import com.transittrack.dto.response.VehicleLocationResponse;
import com.transittrack.entity.VehicleLocationLog;

/**
 * Service interface managing real-time GPS telemetry ingestion, logging, and WebSocket broadcasting.
 */
public interface TrackingService {

    VehicleLocationResponse processLocationUpdate(LocationUpdateRequest request);

    PagedResponse<VehicleLocationLog> getVehicleLocationHistory(Long vehicleId, int page, int size);
}

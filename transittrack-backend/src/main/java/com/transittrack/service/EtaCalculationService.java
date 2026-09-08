package com.transittrack.service;

import com.transittrack.dto.response.StopEtaResponse;

import java.util.List;

/**
 * Service interface for calculating Estimated Time of Arrival (ETA)
 * based on vehicle telemetry, distance, speed, and route stop sequences.
 */
public interface EtaCalculationService {

    List<StopEtaResponse> calculateEtaForVehicle(Long vehicleId);

    List<StopEtaResponse> calculateEtaForRouteStop(Long routeId, Long stopId);
}

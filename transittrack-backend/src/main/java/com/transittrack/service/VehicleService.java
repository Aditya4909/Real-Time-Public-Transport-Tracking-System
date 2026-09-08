package com.transittrack.service;

import com.transittrack.dto.request.VehicleCreateRequest;
import com.transittrack.dto.request.VehicleUpdateRequest;
import com.transittrack.dto.response.PagedResponse;
import com.transittrack.dto.response.VehicleLocationResponse;
import com.transittrack.dto.response.VehicleResponse;

import java.util.List;

/**
 * Service interface managing fleet vehicles, driver assignments, and route allocations.
 */
public interface VehicleService {

    VehicleResponse createVehicle(VehicleCreateRequest request);

    VehicleResponse getVehicleById(Long id);

    PagedResponse<VehicleResponse> getAllVehicles(int page, int size, String sortBy, String sortDir);

    VehicleResponse updateVehicle(Long id, VehicleUpdateRequest request);

    void deleteVehicle(Long id);

    VehicleResponse assignDriver(Long vehicleId, Long driverId);

    VehicleResponse removeDriver(Long vehicleId);

    VehicleResponse assignRoute(Long vehicleId, Long routeId);

    VehicleResponse removeRoute(Long vehicleId);

    VehicleLocationResponse getVehicleLocation(Long vehicleId);

    List<VehicleResponse> getActiveVehicles();
}

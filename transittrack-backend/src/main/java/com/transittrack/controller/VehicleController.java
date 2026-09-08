package com.transittrack.controller;

import com.transittrack.dto.request.AssignDriverRequest;
import com.transittrack.dto.request.AssignRouteRequest;
import com.transittrack.dto.request.VehicleCreateRequest;
import com.transittrack.dto.request.VehicleUpdateRequest;
import com.transittrack.dto.response.*;
import com.transittrack.service.EtaCalculationService;
import com.transittrack.service.VehicleService;
import com.transittrack.util.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller managing transit vehicles, driver assignments, route allocations, and telemetry queries.
 */
@RestController
@RequestMapping("/api/v1/vehicles")
@Tag(name = "Vehicle Management", description = "Endpoints for fleet management, assignments, and telemetry")
public class VehicleController {

    private final VehicleService vehicleService;
    private final EtaCalculationService etaCalculationService;

    public VehicleController(VehicleService vehicleService,
                             EtaCalculationService etaCalculationService) {
        this.vehicleService = vehicleService;
        this.etaCalculationService = etaCalculationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add vehicle to fleet", description = "Admin-only endpoint to register a new vehicle")
    public ResponseEntity<ApiResponse<VehicleResponse>> createVehicle(@Valid @RequestBody VehicleCreateRequest request) {
        VehicleResponse response = vehicleService.createVehicle(request);
        return new ResponseEntity<>(ApiResponse.success("Vehicle registered successfully", response), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all vehicles (paginated)", description = "Retrieves a paginated list of transit vehicles")
    public ResponseEntity<ApiResponse<PagedResponse<VehicleResponse>>> getAllVehicles(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {

        PagedResponse<VehicleResponse> response = vehicleService.getAllVehicles(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Vehicles retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle details", description = "Retrieves details of a specific vehicle by its ID")
    public ResponseEntity<ApiResponse<VehicleResponse>> getVehicleById(@PathVariable Long id) {
        VehicleResponse response = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(ApiResponse.success("Vehicle details retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update vehicle", description = "Admin-only endpoint to update vehicle metadata and status")
    public ResponseEntity<ApiResponse<VehicleResponse>> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleUpdateRequest request) {
        VehicleResponse response = vehicleService.updateVehicle(id, request);
        return ResponseEntity.ok(ApiResponse.success("Vehicle updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete vehicle", description = "Admin-only endpoint to remove a vehicle from fleet")
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok(ApiResponse.success("Vehicle deleted successfully"));
    }

    @PostMapping("/{id}/assign-driver")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign driver to vehicle", description = "Admin-only endpoint to assign a licensed driver to a vehicle")
    public ResponseEntity<ApiResponse<VehicleResponse>> assignDriver(
            @PathVariable Long id,
            @Valid @RequestBody AssignDriverRequest request) {
        VehicleResponse response = vehicleService.assignDriver(id, request.getDriverId());
        return ResponseEntity.ok(ApiResponse.success("Driver assigned to vehicle successfully", response));
    }

    @DeleteMapping("/{id}/driver")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove driver assignment", description = "Admin-only endpoint to unassign a driver from a vehicle")
    public ResponseEntity<ApiResponse<VehicleResponse>> removeDriver(@PathVariable Long id) {
        VehicleResponse response = vehicleService.removeDriver(id);
        return ResponseEntity.ok(ApiResponse.success("Driver unassigned successfully", response));
    }

    @PostMapping("/{id}/assign-route")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign vehicle to route", description = "Admin-only endpoint to allocate a vehicle to a transit route")
    public ResponseEntity<ApiResponse<VehicleResponse>> assignRoute(
            @PathVariable Long id,
            @Valid @RequestBody AssignRouteRequest request) {
        VehicleResponse response = vehicleService.assignRoute(id, request.getRouteId());
        return ResponseEntity.ok(ApiResponse.success("Vehicle assigned to route successfully", response));
    }

    @DeleteMapping("/{id}/route")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove route assignment", description = "Admin-only endpoint to unassign vehicle from its route")
    public ResponseEntity<ApiResponse<VehicleResponse>> removeRoute(@PathVariable Long id) {
        VehicleResponse response = vehicleService.removeRoute(id);
        return ResponseEntity.ok(ApiResponse.success("Route unassigned successfully", response));
    }

    @GetMapping("/{id}/location")
    @Operation(summary = "Get latest vehicle location", description = "Returns the latest GPS coordinates, speed, and heading of a vehicle")
    public ResponseEntity<ApiResponse<VehicleLocationResponse>> getVehicleLocation(@PathVariable Long id) {
        VehicleLocationResponse response = vehicleService.getVehicleLocation(id);
        return ResponseEntity.ok(ApiResponse.success("Vehicle location retrieved successfully", response));
    }

    @GetMapping("/{id}/eta")
    @Operation(summary = "Calculate vehicle arrival ETA", description = "Calculates arrival times for all stops on this vehicle's assigned route based on live GPS coordinates and speed")
    public ResponseEntity<ApiResponse<List<StopEtaResponse>>> getVehicleEta(@PathVariable Long id) {
        List<StopEtaResponse> etas = etaCalculationService.calculateEtaForVehicle(id);
        return ResponseEntity.ok(ApiResponse.success("Vehicle ETAs calculated successfully", etas));
    }
}

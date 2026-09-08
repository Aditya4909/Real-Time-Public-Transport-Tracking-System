package com.transittrack.controller;

import com.transittrack.dto.request.LocationUpdateRequest;
import com.transittrack.dto.response.ApiResponse;
import com.transittrack.dto.response.PagedResponse;
import com.transittrack.dto.response.VehicleLocationResponse;
import com.transittrack.entity.VehicleLocationLog;
import com.transittrack.service.TrackingService;
import com.transittrack.util.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller handling real-time GPS telemetry submissions and location history queries.
 */
@RestController
@RequestMapping("/api/v1/tracking")
@Tag(name = "Telemetry & Tracking", description = "Endpoints for transmitting live GPS locations and retrieving telemetry history")
public class TrackingController {

    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @PostMapping("/location")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN')")
    @Operation(summary = "Submit live GPS location", description = "Driver or Admin endpoint to transmit real-time vehicle coordinates, speed, and heading. Updates DB and broadcasts to WebSocket clients.")
    public ResponseEntity<ApiResponse<VehicleLocationResponse>> updateLocation(@Valid @RequestBody LocationUpdateRequest request) {
        VehicleLocationResponse response = trackingService.processLocationUpdate(request);
        return ResponseEntity.ok(ApiResponse.success("Location updated and broadcasted successfully", response));
    }

    @GetMapping("/vehicles/{id}/history")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get vehicle location history", description = "Admin-only endpoint retrieving time-series telemetry audit logs for a vehicle")
    public ResponseEntity<ApiResponse<PagedResponse<VehicleLocationLog>>> getLocationHistory(
            @PathVariable Long id,
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {

        PagedResponse<VehicleLocationLog> history = trackingService.getVehicleLocationHistory(id, page, size);
        return ResponseEntity.ok(ApiResponse.success("Location history retrieved successfully", history));
    }
}

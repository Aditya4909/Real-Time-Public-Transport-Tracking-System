package com.transittrack.controller;

import com.transittrack.dto.response.ApiResponse;
import com.transittrack.dto.response.DashboardStatsResponse;
import com.transittrack.dto.response.VehicleLocationResponse;
import com.transittrack.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller providing operational metrics and live monitoring data for the Administrator Dashboard.
 */
@RestController
@RequestMapping("/api/v1/admin/dashboard")
@Tag(name = "Admin Dashboard", description = "Endpoints for administrator analytics, fleet telemetry snapshots, and route health")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    @Operation(summary = "Get system statistics", description = "Admin-only endpoint returning high-level fleet, route, stop, and driver metrics")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getStats() {
        DashboardStatsResponse stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard metrics retrieved successfully", stats));
    }

    @GetMapping("/active-vehicles")
    @Operation(summary = "Get active vehicles location map", description = "Admin-only endpoint returning real-time positions for all currently active vehicles")
    public ResponseEntity<ApiResponse<List<VehicleLocationResponse>>> getActiveVehicles() {
        List<VehicleLocationResponse> activeVehicles = dashboardService.getActiveVehiclesLocation();
        return ResponseEntity.ok(ApiResponse.success("Active vehicles telemetry snapshot retrieved successfully", activeVehicles));
    }
}

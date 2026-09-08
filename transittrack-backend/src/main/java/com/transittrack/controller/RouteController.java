package com.transittrack.controller;

import com.transittrack.dto.request.AddRouteStopRequest;
import com.transittrack.dto.request.RouteCreateRequest;
import com.transittrack.dto.response.*;
import com.transittrack.service.EtaCalculationService;
import com.transittrack.service.RouteService;
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
 * Controller managing transit routes, stop sequences, and route query search APIs.
 */
@RestController
@RequestMapping("/api/v1/routes")
@Tag(name = "Route Management", description = "Endpoints for transit routes, stop associations, and passenger searches")
public class RouteController {

    private final RouteService routeService;
    private final EtaCalculationService etaCalculationService;

    public RouteController(RouteService routeService,
                           EtaCalculationService etaCalculationService) {
        this.routeService = routeService;
        this.etaCalculationService = etaCalculationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create transit route", description = "Admin-only endpoint to define a new transit route")
    public ResponseEntity<ApiResponse<RouteResponse>> createRoute(@Valid @RequestBody RouteCreateRequest request) {
        RouteResponse response = routeService.createRoute(request);
        return new ResponseEntity<>(ApiResponse.success("Route created successfully", response), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all routes (paginated)", description = "Retrieves a paginated list of transit routes")
    public ResponseEntity<ApiResponse<PagedResponse<RouteResponse>>> getAllRoutes(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {

        PagedResponse<RouteResponse> response = routeService.getAllRoutes(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Routes retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get route details", description = "Retrieves route information including ordered stop sequence and active vehicle count")
    public ResponseEntity<ApiResponse<RouteResponse>> getRouteById(@PathVariable Long id) {
        RouteResponse response = routeService.getRouteById(id);
        return ResponseEntity.ok(ApiResponse.success("Route details retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update route", description = "Admin-only endpoint to update route properties")
    public ResponseEntity<ApiResponse<RouteResponse>> updateRoute(
            @PathVariable Long id,
            @Valid @RequestBody RouteCreateRequest request) {
        RouteResponse response = routeService.updateRoute(id, request);
        return ResponseEntity.ok(ApiResponse.success("Route updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete route", description = "Admin-only endpoint to remove a transit route")
    public ResponseEntity<ApiResponse<Void>> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.ok(ApiResponse.success("Route deleted successfully"));
    }

    @PostMapping("/{id}/stops")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add stop to route", description = "Admin-only endpoint to insert a stop into a route with sequence and distance")
    public ResponseEntity<ApiResponse<RouteResponse>> addStopToRoute(
            @PathVariable Long id,
            @Valid @RequestBody AddRouteStopRequest request) {
        RouteResponse response = routeService.addStopToRoute(id, request);
        return ResponseEntity.ok(ApiResponse.success("Stop added to route successfully", response));
    }

    @DeleteMapping("/{id}/stops/{stopId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove stop from route", description = "Admin-only endpoint to remove a stop from a route's sequence")
    public ResponseEntity<ApiResponse<RouteResponse>> removeStopFromRoute(
            @PathVariable Long id,
            @PathVariable Long stopId) {
        RouteResponse response = routeService.removeStopFromRoute(id, stopId);
        return ResponseEntity.ok(ApiResponse.success("Stop removed from route successfully", response));
    }

    @GetMapping("/search")
    @Operation(summary = "Search routes", description = "Public endpoint to search routes by keyword (matches name, route number, origin, or destination)")
    public ResponseEntity<ApiResponse<List<RouteResponse>>> searchRoutes(@RequestParam String keyword) {
        List<RouteResponse> responses = routeService.searchRoutes(keyword);
        return ResponseEntity.ok(ApiResponse.success("Routes matching query retrieved successfully", responses));
    }

    @GetMapping("/{id}/vehicles")
    @Operation(summary = "Get vehicles on route", description = "Retrieves all vehicles assigned to this route and their real-time telemetry")
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getVehiclesOnRoute(@PathVariable Long id) {
        List<VehicleResponse> vehicles = routeService.getVehiclesOnRoute(id);
        return ResponseEntity.ok(ApiResponse.success("Vehicles on route retrieved successfully", vehicles));
    }

    @GetMapping("/{id}/eta")
    @Operation(summary = "Get vehicle arrival ETAs for stop", description = "Retrieves arrival times of all active vehicles approaching a given stop on this route")
    public ResponseEntity<ApiResponse<List<StopEtaResponse>>> getStopEtas(
            @PathVariable Long id,
            @RequestParam Long stopId) {
        List<StopEtaResponse> etas = etaCalculationService.calculateEtaForRouteStop(id, stopId);
        return ResponseEntity.ok(ApiResponse.success("Arrival ETAs retrieved successfully", etas));
    }
}

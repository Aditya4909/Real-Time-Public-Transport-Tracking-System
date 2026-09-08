package com.transittrack.controller;

import com.transittrack.dto.request.StopCreateRequest;
import com.transittrack.dto.response.ApiResponse;
import com.transittrack.dto.response.PagedResponse;
import com.transittrack.dto.response.StopResponse;
import com.transittrack.service.StopService;
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
 * Controller managing transit stops, locations, and nearby stop geospatial queries.
 */
@RestController
@RequestMapping("/api/v1/stops")
@Tag(name = "Stop Management", description = "Endpoints for transit stops, coordinates, and nearby discovery")
public class StopController {

    private final StopService stopService;

    public StopController(StopService stopService) {
        this.stopService = stopService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create transit stop", description = "Admin-only endpoint to define a new transit stop with GPS coordinates")
    public ResponseEntity<ApiResponse<StopResponse>> createStop(@Valid @RequestBody StopCreateRequest request) {
        StopResponse response = stopService.createStop(request);
        return new ResponseEntity<>(ApiResponse.success("Transit stop created successfully", response), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all stops (paginated)", description = "Retrieves a paginated list of transit stops")
    public ResponseEntity<ApiResponse<PagedResponse<StopResponse>>> getAllStops(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {

        PagedResponse<StopResponse> response = stopService.getAllStops(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Stops retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get stop details", description = "Retrieves details of a specific stop by its ID")
    public ResponseEntity<ApiResponse<StopResponse>> getStopById(@PathVariable Long id) {
        StopResponse response = stopService.getStopById(id);
        return ResponseEntity.ok(ApiResponse.success("Stop details retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update stop", description = "Admin-only endpoint to update stop coordinates and details")
    public ResponseEntity<ApiResponse<StopResponse>> updateStop(
            @PathVariable Long id,
            @Valid @RequestBody StopCreateRequest request) {
        StopResponse response = stopService.updateStop(id, request);
        return ResponseEntity.ok(ApiResponse.success("Stop updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete stop", description = "Admin-only endpoint to remove a transit stop")
    public ResponseEntity<ApiResponse<Void>> deleteStop(@PathVariable Long id) {
        stopService.deleteStop(id);
        return ResponseEntity.ok(ApiResponse.success("Stop deleted successfully"));
    }

    @GetMapping("/search")
    @Operation(summary = "Search stops", description = "Public endpoint to search stops by name or stop code")
    public ResponseEntity<ApiResponse<List<StopResponse>>> searchStops(@RequestParam String query) {
        List<StopResponse> stops = stopService.searchStops(query);
        return ResponseEntity.ok(ApiResponse.success("Stops matching query retrieved successfully", stops));
    }

    @GetMapping("/nearby")
    @Operation(summary = "Find nearby stops", description = "Finds all transit stops within a specified radius (in kilometers) of given GPS coordinates")
    public ResponseEntity<ApiResponse<List<StopResponse>>> findNearbyStops(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false, defaultValue = "5.0") Double radiusKm) {
        List<StopResponse> stops = stopService.findNearbyStops(latitude, longitude, radiusKm);
        return ResponseEntity.ok(ApiResponse.success("Nearby stops retrieved successfully", stops));
    }
}

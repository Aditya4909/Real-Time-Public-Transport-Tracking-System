package com.transittrack.service;

import com.transittrack.dto.request.AddRouteStopRequest;
import com.transittrack.dto.request.RouteCreateRequest;
import com.transittrack.dto.response.PagedResponse;
import com.transittrack.dto.response.RouteResponse;
import com.transittrack.dto.response.VehicleResponse;

import java.util.List;

/**
 * Service interface managing routes, stop sequences, and route vehicle assignments.
 */
public interface RouteService {

    RouteResponse createRoute(RouteCreateRequest request);

    RouteResponse getRouteById(Long id);

    PagedResponse<RouteResponse> getAllRoutes(int page, int size, String sortBy, String sortDir);

    RouteResponse updateRoute(Long id, RouteCreateRequest request);

    void deleteRoute(Long id);

    RouteResponse addStopToRoute(Long routeId, AddRouteStopRequest request);

    RouteResponse removeStopFromRoute(Long routeId, Long stopId);

    List<RouteResponse> searchRoutes(String keyword);

    List<RouteResponse> findRoutesByOriginAndDestination(String origin, String destination);

    List<VehicleResponse> getVehiclesOnRoute(Long routeId);
}

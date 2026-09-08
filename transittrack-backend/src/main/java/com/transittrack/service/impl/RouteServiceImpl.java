package com.transittrack.service.impl;

import com.transittrack.dto.request.AddRouteStopRequest;
import com.transittrack.dto.request.RouteCreateRequest;
import com.transittrack.dto.response.PagedResponse;
import com.transittrack.dto.response.RouteResponse;
import com.transittrack.dto.response.RouteStopResponse;
import com.transittrack.dto.response.VehicleResponse;
import com.transittrack.entity.*;
import com.transittrack.exception.BadRequestException;
import com.transittrack.exception.ResourceNotFoundException;
import com.transittrack.repository.RouteRepository;
import com.transittrack.repository.RouteStopRepository;
import com.transittrack.repository.StopRepository;
import com.transittrack.repository.VehicleRepository;
import com.transittrack.service.RouteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of RouteService for route definition, stop sequence management, and route queries.
 */
@Service
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final StopRepository stopRepository;
    private final RouteStopRepository routeStopRepository;
    private final VehicleRepository vehicleRepository;

    public RouteServiceImpl(RouteRepository routeRepository,
                            StopRepository stopRepository,
                            RouteStopRepository routeStopRepository,
                            VehicleRepository vehicleRepository) {
        this.routeRepository = routeRepository;
        this.stopRepository = stopRepository;
        this.routeStopRepository = routeStopRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    @Transactional
    public RouteResponse createRoute(RouteCreateRequest request) {
        if (routeRepository.existsByRouteNumber(request.getRouteNumber())) {
            throw new BadRequestException("Route with number '" + request.getRouteNumber() + "' already exists!");
        }

        Route route = new Route(
                request.getRouteNumber().toUpperCase().trim(),
                request.getName().trim(),
                request.getOrigin().trim(),
                request.getDestination().trim(),
                request.getTotalDistanceKm(),
                request.getEstimatedDurationMinutes()
        );
        route.setActive(request.isActive());

        Route savedRoute = routeRepository.save(route);
        return mapRouteToResponse(savedRoute);
    }

    @Override
    @Transactional(readOnly = true)
    public RouteResponse getRouteById(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));
        return mapRouteToResponse(route);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RouteResponse> getAllRoutes(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Route> routes = routeRepository.findAll(pageable);
        List<RouteResponse> content = routes.getContent().stream()
                .map(this::mapRouteToResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(content, routes.getNumber(), routes.getSize(), routes.getTotalElements(), routes.getTotalPages(), routes.isLast());
    }

    @Override
    @Transactional
    public RouteResponse updateRoute(Long id, RouteCreateRequest request) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));

        if (!route.getRouteNumber().equalsIgnoreCase(request.getRouteNumber()) &&
                routeRepository.existsByRouteNumber(request.getRouteNumber())) {
            throw new BadRequestException("Route number '" + request.getRouteNumber() + "' is already in use");
        }

        route.setRouteNumber(request.getRouteNumber().toUpperCase().trim());
        route.setName(request.getName().trim());
        route.setOrigin(request.getOrigin().trim());
        route.setDestination(request.getDestination().trim());
        route.setTotalDistanceKm(request.getTotalDistanceKm());
        route.setEstimatedDurationMinutes(request.getEstimatedDurationMinutes());
        route.setActive(request.isActive());

        Route updatedRoute = routeRepository.save(route);
        return mapRouteToResponse(updatedRoute);
    }

    @Override
    @Transactional
    public void deleteRoute(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));
        routeRepository.delete(route);
    }

    @Override
    @Transactional
    public RouteResponse addStopToRoute(Long routeId, AddRouteStopRequest request) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", routeId));

        Stop stop = stopRepository.findById(request.getStopId())
                .orElseThrow(() -> new ResourceNotFoundException("Stop", "id", request.getStopId()));

        if (routeStopRepository.findByRouteIdAndStopId(routeId, request.getStopId()).isPresent()) {
            throw new BadRequestException("Stop is already part of this route");
        }

        if (routeStopRepository.findByRouteIdAndStopSequence(routeId, request.getStopSequence()).isPresent()) {
            throw new BadRequestException("Sequence position " + request.getStopSequence() + " is already occupied on this route");
        }

        RouteStop routeStop = new RouteStop(
                route,
                stop,
                request.getStopSequence(),
                request.getDistanceFromPrevStopKm(),
                request.getTravelTimeFromPrevMinutes()
        );

        route.addRouteStop(routeStop);
        routeStopRepository.save(routeStop);

        Route savedRoute = routeRepository.findById(routeId).orElseThrow();
        return mapRouteToResponse(savedRoute);
    }

    @Override
    @Transactional
    public RouteResponse removeStopFromRoute(Long routeId, Long stopId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", routeId));

        RouteStop routeStop = routeStopRepository.findByRouteIdAndStopId(routeId, stopId)
                .orElseThrow(() -> new ResourceNotFoundException("Stop with id " + stopId + " not found on route " + routeId));

        route.removeRouteStop(routeStop);
        routeStopRepository.delete(routeStop);

        Route savedRoute = routeRepository.findById(routeId).orElseThrow();
        return mapRouteToResponse(savedRoute);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteResponse> searchRoutes(String keyword) {
        return routeRepository.searchRoutes(keyword).stream()
                .map(this::mapRouteToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteResponse> findRoutesByOriginAndDestination(String origin, String destination) {
        return routeRepository.findByOriginAndDestination(origin, destination).stream()
                .map(this::mapRouteToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getVehiclesOnRoute(Long routeId) {
        if (!routeRepository.existsById(routeId)) {
            throw new ResourceNotFoundException("Route", "id", routeId);
        }

        return vehicleRepository.findByRouteId(routeId).stream()
                .map(VehicleServiceImpl::mapVehicleToResponse)
                .collect(Collectors.toList());
    }

    public RouteResponse mapRouteToResponse(Route route) {
        List<RouteStopResponse> stopResponses = route.getRouteStops().stream()
                .map(rs -> new RouteStopResponse(
                        rs.getId(),
                        rs.getStop().getId(),
                        rs.getStop().getCode(),
                        rs.getStop().getName(),
                        rs.getStop().getLatitude(),
                        rs.getStop().getLongitude(),
                        rs.getStop().getAddress(),
                        rs.getStopSequence(),
                        rs.getDistanceFromPrevStopKm(),
                        rs.getTravelTimeFromPrevMinutes()
                ))
                .collect(Collectors.toList());

        int activeVehiclesCount = (int) vehicleRepository.findByRouteIdAndStatus(route.getId(), VehicleStatus.ACTIVE).size();

        return new RouteResponse(
                route.getId(),
                route.getRouteNumber(),
                route.getName(),
                route.getOrigin(),
                route.getDestination(),
                route.getTotalDistanceKm(),
                route.getEstimatedDurationMinutes(),
                route.isActive(),
                stopResponses,
                activeVehiclesCount,
                route.getCreatedAt()
        );
    }
}

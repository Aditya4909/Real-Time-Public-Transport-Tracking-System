package com.transittrack.service.impl;

import com.transittrack.dto.request.VehicleCreateRequest;
import com.transittrack.dto.request.VehicleUpdateRequest;
import com.transittrack.dto.response.PagedResponse;
import com.transittrack.dto.response.UserResponse;
import com.transittrack.dto.response.VehicleLocationResponse;
import com.transittrack.dto.response.VehicleResponse;
import com.transittrack.entity.*;
import com.transittrack.exception.BadRequestException;
import com.transittrack.exception.ResourceNotFoundException;
import com.transittrack.repository.RouteRepository;
import com.transittrack.repository.UserRepository;
import com.transittrack.repository.VehicleRepository;
import com.transittrack.service.VehicleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of VehicleService for fleet management, driver assignment, and route allocation.
 */
@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final RouteRepository routeRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository,
                              UserRepository userRepository,
                              RouteRepository routeRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.routeRepository = routeRepository;
    }

    @Override
    @Transactional
    public VehicleResponse createVehicle(VehicleCreateRequest request) {
        if (vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new BadRequestException("Vehicle with registration number '" + request.getRegistrationNumber() + "' already exists!");
        }

        Vehicle vehicle = new Vehicle(
                request.getRegistrationNumber().toUpperCase().trim(),
                request.getModel().trim(),
                request.getCapacity(),
                request.getStatus() != null ? request.getStatus() : VehicleStatus.INACTIVE
        );

        if (request.getDriverId() != null) {
            User driver = validateAndGetDriver(request.getDriverId(), null);
            vehicle.setDriver(driver);
        }

        if (request.getRouteId() != null) {
            Route route = routeRepository.findById(request.getRouteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Route", "id", request.getRouteId()));
            vehicle.setRoute(route);
        }

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return mapVehicleToResponse(savedVehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
        return mapVehicleToResponse(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<VehicleResponse> getAllVehicles(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Vehicle> vehicles = vehicleRepository.findAll(pageable);
        List<VehicleResponse> content = vehicles.getContent().stream()
                .map(VehicleServiceImpl::mapVehicleToResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(content, vehicles.getNumber(), vehicles.getSize(), vehicles.getTotalElements(), vehicles.getTotalPages(), vehicles.isLast());
    }

    @Override
    @Transactional
    public VehicleResponse updateVehicle(Long id, VehicleUpdateRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));

        if (!vehicle.getRegistrationNumber().equalsIgnoreCase(request.getRegistrationNumber()) &&
                vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new BadRequestException("Registration number '" + request.getRegistrationNumber() + "' is already in use");
        }

        vehicle.setRegistrationNumber(request.getRegistrationNumber().toUpperCase().trim());
        vehicle.setModel(request.getModel().trim());
        vehicle.setCapacity(request.getCapacity());
        vehicle.setStatus(request.getStatus());

        if (request.getDriverId() != null) {
            User driver = validateAndGetDriver(request.getDriverId(), id);
            vehicle.setDriver(driver);
        } else {
            vehicle.setDriver(null);
        }

        if (request.getRouteId() != null) {
            Route route = routeRepository.findById(request.getRouteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Route", "id", request.getRouteId()));
            vehicle.setRoute(route);
        } else {
            vehicle.setRoute(null);
        }

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return mapVehicleToResponse(updatedVehicle);
    }

    @Override
    @Transactional
    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
        vehicleRepository.delete(vehicle);
    }

    @Override
    @Transactional
    public VehicleResponse assignDriver(Long vehicleId, Long driverId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", vehicleId));

        User driver = validateAndGetDriver(driverId, vehicleId);
        vehicle.setDriver(driver);

        Vehicle saved = vehicleRepository.save(vehicle);
        return mapVehicleToResponse(saved);
    }

    @Override
    @Transactional
    public VehicleResponse removeDriver(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", vehicleId));

        vehicle.setDriver(null);
        Vehicle saved = vehicleRepository.save(vehicle);
        return mapVehicleToResponse(saved);
    }

    @Override
    @Transactional
    public VehicleResponse assignRoute(Long vehicleId, Long routeId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", vehicleId));

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", routeId));

        vehicle.setRoute(route);
        Vehicle saved = vehicleRepository.save(vehicle);
        return mapVehicleToResponse(saved);
    }

    @Override
    @Transactional
    public VehicleResponse removeRoute(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", vehicleId));

        vehicle.setRoute(null);
        Vehicle saved = vehicleRepository.save(vehicle);
        return mapVehicleToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleLocationResponse getVehicleLocation(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", vehicleId));

        if (vehicle.getCurrentLatitude() == null || vehicle.getCurrentLongitude() == null) {
            throw new BadRequestException("No location data available yet for vehicle: " + vehicle.getRegistrationNumber());
        }

        Long routeId = vehicle.getRoute() != null ? vehicle.getRoute().getId() : null;
        String routeNumber = vehicle.getRoute() != null ? vehicle.getRoute().getRouteNumber() : null;

        return new VehicleLocationResponse(
                vehicle.getId(),
                vehicle.getRegistrationNumber(),
                routeId,
                routeNumber,
                vehicle.getCurrentLatitude(),
                vehicle.getCurrentLongitude(),
                vehicle.getCurrentSpeedKmh(),
                vehicle.getHeading(),
                vehicle.getLastLocationUpdate()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getActiveVehicles() {
        return vehicleRepository.findByStatus(VehicleStatus.ACTIVE).stream()
                .map(VehicleServiceImpl::mapVehicleToResponse)
                .collect(Collectors.toList());
    }

    private User validateAndGetDriver(Long driverId, Long currentVehicleId) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", driverId));

        boolean hasDriverRole = driver.getRoles().stream()
                .anyMatch(r -> r.getName() == ERole.ROLE_DRIVER);
        if (!hasDriverRole) {
            throw new BadRequestException("User '" + driver.getUsername() + "' does not possess the DRIVER role");
        }

        Optional<Vehicle> existingVehicle = vehicleRepository.findByDriverId(driverId);
        if (existingVehicle.isPresent() && !existingVehicle.get().getId().equals(currentVehicleId)) {
            throw new BadRequestException("Driver '" + driver.getUsername() + "' is already assigned to vehicle: " + existingVehicle.get().getRegistrationNumber());
        }

        return driver;
    }

    public static VehicleResponse mapVehicleToResponse(Vehicle vehicle) {
        VehicleResponse response = new VehicleResponse();
        response.setId(vehicle.getId());
        response.setRegistrationNumber(vehicle.getRegistrationNumber());
        response.setModel(vehicle.getModel());
        response.setCapacity(vehicle.getCapacity());
        response.setStatus(vehicle.getStatus());

        if (vehicle.getDriver() != null) {
            response.setDriver(AuthServiceImpl.mapUserToResponse(vehicle.getDriver()));
        }

        if (vehicle.getRoute() != null) {
            response.setRouteId(vehicle.getRoute().getId());
            response.setRouteNumber(vehicle.getRoute().getRouteNumber());
            response.setRouteName(vehicle.getRoute().getName());
        }

        response.setCurrentLatitude(vehicle.getCurrentLatitude());
        response.setCurrentLongitude(vehicle.getCurrentLongitude());
        response.setCurrentSpeedKmh(vehicle.getCurrentSpeedKmh());
        response.setHeading(vehicle.getHeading());
        response.setLastLocationUpdate(vehicle.getLastLocationUpdate());
        response.setCreatedAt(vehicle.getCreatedAt());

        return response;
    }
}

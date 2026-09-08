package com.transittrack.service.impl;

import com.transittrack.dto.response.DashboardStatsResponse;
import com.transittrack.dto.response.VehicleLocationResponse;
import com.transittrack.entity.ERole;
import com.transittrack.entity.Vehicle;
import com.transittrack.entity.VehicleStatus;
import com.transittrack.repository.RouteRepository;
import com.transittrack.repository.StopRepository;
import com.transittrack.repository.UserRepository;
import com.transittrack.repository.VehicleRepository;
import com.transittrack.service.AdminDashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of AdminDashboardService providing administrative overview metrics.
 */
@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final VehicleRepository vehicleRepository;
    private final RouteRepository routeRepository;
    private final StopRepository stopRepository;
    private final UserRepository userRepository;

    public AdminDashboardServiceImpl(VehicleRepository vehicleRepository,
                                     RouteRepository routeRepository,
                                     StopRepository stopRepository,
                                     UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.routeRepository = routeRepository;
        this.stopRepository = stopRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        long totalVehicles = vehicleRepository.count();
        long activeVehicles = vehicleRepository.countByStatus(VehicleStatus.ACTIVE);
        long maintenanceVehicles = vehicleRepository.countByStatus(VehicleStatus.MAINTENANCE);
        long inactiveVehicles = vehicleRepository.countByStatus(VehicleStatus.INACTIVE);

        long totalRoutes = routeRepository.count();
        long activeRoutes = routeRepository.countByActive(true);

        long totalStops = stopRepository.count();

        long totalDrivers = userRepository.countByRoles_Name(ERole.ROLE_DRIVER);
        long activeDrivers = vehicleRepository.findAll().stream()
                .filter(v -> v.getStatus() == VehicleStatus.ACTIVE && v.getDriver() != null)
                .count();

        return new DashboardStatsResponse(
                totalVehicles,
                activeVehicles,
                maintenanceVehicles,
                inactiveVehicles,
                totalRoutes,
                activeRoutes,
                totalStops,
                totalDrivers,
                activeDrivers
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleLocationResponse> getActiveVehiclesLocation() {
        List<Vehicle> activeVehicles = vehicleRepository.findByStatus(VehicleStatus.ACTIVE);

        return activeVehicles.stream()
                .filter(v -> v.getCurrentLatitude() != null && v.getCurrentLongitude() != null)
                .map(v -> new VehicleLocationResponse(
                        v.getId(),
                        v.getRegistrationNumber(),
                        v.getRoute() != null ? v.getRoute().getId() : null,
                        v.getRoute() != null ? v.getRoute().getRouteNumber() : null,
                        v.getCurrentLatitude(),
                        v.getCurrentLongitude(),
                        v.getCurrentSpeedKmh(),
                        v.getHeading(),
                        v.getLastLocationUpdate()
                ))
                .collect(Collectors.toList());
    }
}

package com.transittrack.service.impl;

import com.transittrack.dto.response.StopEtaResponse;
import com.transittrack.entity.*;
import com.transittrack.exception.BadRequestException;
import com.transittrack.exception.ResourceNotFoundException;
import com.transittrack.repository.RouteRepository;
import com.transittrack.repository.RouteStopRepository;
import com.transittrack.repository.StopRepository;
import com.transittrack.repository.VehicleRepository;
import com.transittrack.service.EtaCalculationService;
import com.transittrack.util.GeoUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service implementation calculating vehicle arrival ETAs using geographical coordinates and speed.
 */
@Service
public class EtaCalculationServiceImpl implements EtaCalculationService {

    private final VehicleRepository vehicleRepository;
    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;
    private final StopRepository stopRepository;

    public EtaCalculationServiceImpl(VehicleRepository vehicleRepository,
                                     RouteRepository routeRepository,
                                     RouteStopRepository routeStopRepository,
                                     StopRepository stopRepository) {
        this.vehicleRepository = vehicleRepository;
        this.routeRepository = routeRepository;
        this.routeStopRepository = routeStopRepository;
        this.stopRepository = stopRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StopEtaResponse> calculateEtaForVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", vehicleId));

        Route route = vehicle.getRoute();
        if (route == null) {
            throw new BadRequestException("Vehicle " + vehicle.getRegistrationNumber() + " is not assigned to any route");
        }

        if (vehicle.getCurrentLatitude() == null || vehicle.getCurrentLongitude() == null) {
            throw new BadRequestException("No GPS telemetry available for vehicle: " + vehicle.getRegistrationNumber());
        }

        List<RouteStop> routeStops = routeStopRepository.findByRouteIdOrderByStopSequenceAsc(route.getId());
        List<StopEtaResponse> etaList = new ArrayList<>();

        double currentLat = vehicle.getCurrentLatitude();
        double currentLon = vehicle.getCurrentLongitude();
        double speed = vehicle.getCurrentSpeedKmh() != null ? vehicle.getCurrentSpeedKmh() : 0.0;
        LocalDateTime now = LocalDateTime.now();

        for (RouteStop routeStop : routeStops) {
            Stop stop = routeStop.getStop();
            double distanceKm = GeoUtils.calculateDistanceKm(currentLat, currentLon, stop.getLatitude(), stop.getLongitude());
            int etaMinutes = GeoUtils.calculateEtaMinutes(distanceKm, speed);
            LocalDateTime etaTime = now.plusMinutes(etaMinutes);

            etaList.add(new StopEtaResponse(
                    stop.getId(),
                    stop.getCode(),
                    stop.getName(),
                    routeStop.getStopSequence(),
                    distanceKm,
                    etaMinutes,
                    etaTime,
                    vehicle.getId(),
                    vehicle.getRegistrationNumber()
            ));
        }

        return etaList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StopEtaResponse> calculateEtaForRouteStop(Long routeId, Long stopId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", routeId));

        Stop targetStop = stopRepository.findById(stopId)
                .orElseThrow(() -> new ResourceNotFoundException("Stop", "id", stopId));

        RouteStop targetRouteStop = routeStopRepository.findByRouteIdAndStopId(routeId, stopId)
                .orElseThrow(() -> new BadRequestException("Stop '" + targetStop.getName() + "' is not on route '" + route.getRouteNumber() + "'"));

        List<Vehicle> activeVehicles = vehicleRepository.findByRouteIdAndStatus(routeId, VehicleStatus.ACTIVE);
        List<StopEtaResponse> etas = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Vehicle vehicle : activeVehicles) {
            if (vehicle.getCurrentLatitude() != null && vehicle.getCurrentLongitude() != null) {
                double distanceKm = GeoUtils.calculateDistanceKm(
                        vehicle.getCurrentLatitude(),
                        vehicle.getCurrentLongitude(),
                        targetStop.getLatitude(),
                        targetStop.getLongitude()
                );

                double speed = vehicle.getCurrentSpeedKmh() != null ? vehicle.getCurrentSpeedKmh() : 0.0;
                int etaMinutes = GeoUtils.calculateEtaMinutes(distanceKm, speed);

                etas.add(new StopEtaResponse(
                        targetStop.getId(),
                        targetStop.getCode(),
                        targetStop.getName(),
                        targetRouteStop.getStopSequence(),
                        distanceKm,
                        etaMinutes,
                        now.plusMinutes(etaMinutes),
                        vehicle.getId(),
                        vehicle.getRegistrationNumber()
                ));
            }
        }

        etas.sort(Comparator.comparingInt(StopEtaResponse::getEstimatedMinutes));
        return etas;
    }
}

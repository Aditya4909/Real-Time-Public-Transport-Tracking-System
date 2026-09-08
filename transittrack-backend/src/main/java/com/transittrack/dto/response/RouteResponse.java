package com.transittrack.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class RouteResponse {

    private Long id;
    private String routeNumber;
    private String name;
    private String origin;
    private String destination;
    private Double totalDistanceKm;
    private Integer estimatedDurationMinutes;
    private boolean active;
    private List<RouteStopResponse> stops;
    private int activeVehiclesCount;
    private LocalDateTime createdAt;

    public RouteResponse() {
    }

    public RouteResponse(Long id, String routeNumber, String name, String origin, String destination,
                         Double totalDistanceKm, Integer estimatedDurationMinutes, boolean active,
                         List<RouteStopResponse> stops, int activeVehiclesCount, LocalDateTime createdAt) {
        this.id = id;
        this.routeNumber = routeNumber;
        this.name = name;
        this.origin = origin;
        this.destination = destination;
        this.totalDistanceKm = totalDistanceKm;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.active = active;
        this.stops = stops;
        this.activeVehiclesCount = activeVehiclesCount;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(Double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
    }

    public Integer getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }

    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) {
        this.estimatedDurationMinutes = estimatedDurationMinutes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<RouteStopResponse> getStops() {
        return stops;
    }

    public void setStops(List<RouteStopResponse> stops) {
        this.stops = stops;
    }

    public int getActiveVehiclesCount() {
        return activeVehiclesCount;
    }

    public void setActiveVehiclesCount(int activeVehiclesCount) {
        this.activeVehiclesCount = activeVehiclesCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

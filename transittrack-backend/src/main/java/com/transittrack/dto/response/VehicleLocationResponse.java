package com.transittrack.dto.response;

import java.time.LocalDateTime;

/**
 * Real-time telemetry payload broadcasted via WebSocket STOMP and REST APIs.
 */
public class VehicleLocationResponse {

    private Long vehicleId;
    private String registrationNumber;
    private Long routeId;
    private String routeNumber;
    private Double latitude;
    private Double longitude;
    private Double speedKmh;
    private Double heading;
    private LocalDateTime timestamp;

    public VehicleLocationResponse() {
    }

    public VehicleLocationResponse(Long vehicleId, String registrationNumber, Long routeId, String routeNumber,
                                   Double latitude, Double longitude, Double speedKmh, Double heading, LocalDateTime timestamp) {
        this.vehicleId = vehicleId;
        this.registrationNumber = registrationNumber;
        this.routeId = routeId;
        this.routeNumber = routeNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speedKmh = speedKmh;
        this.heading = heading;
        this.timestamp = timestamp;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getSpeedKmh() {
        return speedKmh;
    }

    public void setSpeedKmh(Double speedKmh) {
        this.speedKmh = speedKmh;
    }

    public Double getHeading() {
        return heading;
    }

    public void setHeading(Double heading) {
        this.heading = heading;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

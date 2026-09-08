package com.transittrack.dto.response;

import java.time.LocalDateTime;

/**
 * DTO representing an estimated time of arrival (ETA) calculation for a vehicle approaching a stop.
 */
public class StopEtaResponse {

    private Long stopId;
    private String stopCode;
    private String stopName;
    private Integer stopSequence;
    private Double distanceToStopKm;
    private Integer estimatedMinutes;
    private LocalDateTime estimatedArrivalTime;
    private Long vehicleId;
    private String vehicleRegistrationNumber;

    public StopEtaResponse() {
    }

    public StopEtaResponse(Long stopId, String stopCode, String stopName, Integer stopSequence,
                           Double distanceToStopKm, Integer estimatedMinutes, LocalDateTime estimatedArrivalTime,
                           Long vehicleId, String vehicleRegistrationNumber) {
        this.stopId = stopId;
        this.stopCode = stopCode;
        this.stopName = stopName;
        this.stopSequence = stopSequence;
        this.distanceToStopKm = distanceToStopKm;
        this.estimatedMinutes = estimatedMinutes;
        this.estimatedArrivalTime = estimatedArrivalTime;
        this.vehicleId = vehicleId;
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
    }

    public Long getStopId() {
        return stopId;
    }

    public void setStopId(Long stopId) {
        this.stopId = stopId;
    }

    public String getStopCode() {
        return stopCode;
    }

    public void setStopCode(String stopCode) {
        this.stopCode = stopCode;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public Integer getStopSequence() {
        return stopSequence;
    }

    public void setStopSequence(Integer stopSequence) {
        this.stopSequence = stopSequence;
    }

    public Double getDistanceToStopKm() {
        return distanceToStopKm;
    }

    public void setDistanceToStopKm(Double distanceToStopKm) {
        this.distanceToStopKm = distanceToStopKm;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public LocalDateTime getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public void setEstimatedArrivalTime(LocalDateTime estimatedArrivalTime) {
        this.estimatedArrivalTime = estimatedArrivalTime;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleRegistrationNumber() {
        return vehicleRegistrationNumber;
    }

    public void setVehicleRegistrationNumber(String vehicleRegistrationNumber) {
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
    }
}

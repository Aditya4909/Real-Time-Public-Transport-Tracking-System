package com.transittrack.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class AddRouteStopRequest {

    @NotNull(message = "Stop ID is required")
    private Long stopId;

    @NotNull(message = "Stop sequence number is required")
    @Min(value = 1, message = "Stop sequence must start at 1")
    private Integer stopSequence;

    @NotNull(message = "Distance from previous stop is required")
    @PositiveOrZero(message = "Distance must be zero or positive")
    private Double distanceFromPrevStopKm = 0.0;

    @NotNull(message = "Travel time from previous stop is required")
    @PositiveOrZero(message = "Travel time must be zero or positive")
    private Integer travelTimeFromPrevMinutes = 0;

    public AddRouteStopRequest() {
    }

    public AddRouteStopRequest(Long stopId, Integer stopSequence, Double distanceFromPrevStopKm, Integer travelTimeFromPrevMinutes) {
        this.stopId = stopId;
        this.stopSequence = stopSequence;
        this.distanceFromPrevStopKm = distanceFromPrevStopKm;
        this.travelTimeFromPrevMinutes = travelTimeFromPrevMinutes;
    }

    public Long getStopId() {
        return stopId;
    }

    public void setStopId(Long stopId) {
        this.stopId = stopId;
    }

    public Integer getStopSequence() {
        return stopSequence;
    }

    public void setStopSequence(Integer stopSequence) {
        this.stopSequence = stopSequence;
    }

    public Double getDistanceFromPrevStopKm() {
        return distanceFromPrevStopKm;
    }

    public void setDistanceFromPrevStopKm(Double distanceFromPrevStopKm) {
        this.distanceFromPrevStopKm = distanceFromPrevStopKm;
    }

    public Integer getTravelTimeFromPrevMinutes() {
        return travelTimeFromPrevMinutes;
    }

    public void setTravelTimeFromPrevMinutes(Integer travelTimeFromPrevMinutes) {
        this.travelTimeFromPrevMinutes = travelTimeFromPrevMinutes;
    }
}

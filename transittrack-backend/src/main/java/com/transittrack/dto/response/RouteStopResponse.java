package com.transittrack.dto.response;

public class RouteStopResponse {

    private Long id;
    private Long stopId;
    private String stopCode;
    private String stopName;
    private Double latitude;
    private Double longitude;
    private String address;
    private Integer stopSequence;
    private Double distanceFromPrevStopKm;
    private Integer travelTimeFromPrevMinutes;

    public RouteStopResponse() {
    }

    public RouteStopResponse(Long id, Long stopId, String stopCode, String stopName, Double latitude, Double longitude,
                             String address, Integer stopSequence, Double distanceFromPrevStopKm, Integer travelTimeFromPrevMinutes) {
        this.id = id;
        this.stopId = stopId;
        this.stopCode = stopCode;
        this.stopName = stopName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.stopSequence = stopSequence;
        this.distanceFromPrevStopKm = distanceFromPrevStopKm;
        this.travelTimeFromPrevMinutes = travelTimeFromPrevMinutes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

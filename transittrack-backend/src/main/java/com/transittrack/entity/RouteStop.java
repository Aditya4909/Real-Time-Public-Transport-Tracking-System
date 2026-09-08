package com.transittrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Association entity representing an ordered stop along a route.
 * Contains sequence ordering and incremental distance/time between consecutive stops.
 */
@Entity
@Table(name = "route_stops", uniqueConstraints = {
        @UniqueConstraint(name = "uk_route_stop_sequence", columnNames = {"route_id", "stop_sequence"}),
        @UniqueConstraint(name = "uk_route_stop", columnNames = {"route_id", "stop_id"})
})
public class RouteStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    @JsonIgnore
    private Route route;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "stop_id", nullable = false)
    private Stop stop;

    @Column(name = "stop_sequence", nullable = false)
    private Integer stopSequence;

    @Column(name = "distance_from_prev_stop_km", nullable = false)
    private Double distanceFromPrevStopKm = 0.0;

    @Column(name = "travel_time_from_prev_minutes", nullable = false)
    private Integer travelTimeFromPrevMinutes = 0;

    public RouteStop() {
    }

    public RouteStop(Route route, Stop stop, Integer stopSequence, Double distanceFromPrevStopKm, Integer travelTimeFromPrevMinutes) {
        this.route = route;
        this.stop = stop;
        this.stopSequence = stopSequence;
        this.distanceFromPrevStopKm = distanceFromPrevStopKm;
        this.travelTimeFromPrevMinutes = travelTimeFromPrevMinutes;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Stop getStop() {
        return stop;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
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

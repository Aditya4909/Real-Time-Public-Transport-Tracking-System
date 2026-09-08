package com.transittrack.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity capturing immutable time-series location snapshots for vehicles.
 * Used for route playback, speed audits, and diagnostics.
 */
@Entity
@Table(name = "vehicle_locations_log", indexes = {
        @Index(name = "idx_veh_loc_log_vehicle_time", columnList = "vehicle_id, recorded_at")
})
public class VehicleLocationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "speed_kmh", nullable = false)
    private Double speedKmh = 0.0;

    @Column(nullable = false)
    private Double heading = 0.0;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    public VehicleLocationLog() {
    }

    public VehicleLocationLog(Vehicle vehicle, Double latitude, Double longitude, Double speedKmh, Double heading, LocalDateTime recordedAt) {
        this.vehicle = vehicle;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speedKmh = speedKmh;
        this.heading = heading;
        this.recordedAt = recordedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
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

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}

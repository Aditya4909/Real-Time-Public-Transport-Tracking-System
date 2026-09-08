package com.transittrack.dto.response;

public class DashboardStatsResponse {

    private long totalVehicles;
    private long activeVehicles;
    private long maintenanceVehicles;
    private long inactiveVehicles;
    private long totalRoutes;
    private long activeRoutes;
    private long totalStops;
    private long totalDrivers;
    private long activeDrivers;

    public DashboardStatsResponse() {
    }

    public DashboardStatsResponse(long totalVehicles, long activeVehicles, long maintenanceVehicles,
                                  long inactiveVehicles, long totalRoutes, long activeRoutes,
                                  long totalStops, long totalDrivers, long activeDrivers) {
        this.totalVehicles = totalVehicles;
        this.activeVehicles = activeVehicles;
        this.maintenanceVehicles = maintenanceVehicles;
        this.inactiveVehicles = inactiveVehicles;
        this.totalRoutes = totalRoutes;
        this.activeRoutes = activeRoutes;
        this.totalStops = totalStops;
        this.totalDrivers = totalDrivers;
        this.activeDrivers = activeDrivers;
    }

    public long getTotalVehicles() {
        return totalVehicles;
    }

    public void setTotalVehicles(long totalVehicles) {
        this.totalVehicles = totalVehicles;
    }

    public long getActiveVehicles() {
        return activeVehicles;
    }

    public void setActiveVehicles(long activeVehicles) {
        this.activeVehicles = activeVehicles;
    }

    public long getMaintenanceVehicles() {
        return maintenanceVehicles;
    }

    public void setMaintenanceVehicles(long maintenanceVehicles) {
        this.maintenanceVehicles = maintenanceVehicles;
    }

    public long getInactiveVehicles() {
        return inactiveVehicles;
    }

    public void setInactiveVehicles(long inactiveVehicles) {
        this.inactiveVehicles = inactiveVehicles;
    }

    public long getTotalRoutes() {
        return totalRoutes;
    }

    public void setTotalRoutes(long totalRoutes) {
        this.totalRoutes = totalRoutes;
    }

    public long getActiveRoutes() {
        return activeRoutes;
    }

    public void setActiveRoutes(long activeRoutes) {
        this.activeRoutes = activeRoutes;
    }

    public long getTotalStops() {
        return totalStops;
    }

    public void setTotalStops(long totalStops) {
        this.totalStops = totalStops;
    }

    public long getTotalDrivers() {
        return totalDrivers;
    }

    public void setTotalDrivers(long totalDrivers) {
        this.totalDrivers = totalDrivers;
    }

    public long getActiveDrivers() {
        return activeDrivers;
    }

    public void setActiveDrivers(long activeDrivers) {
        this.activeDrivers = activeDrivers;
    }
}

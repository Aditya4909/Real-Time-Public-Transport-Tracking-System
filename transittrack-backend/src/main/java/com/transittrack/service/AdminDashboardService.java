package com.transittrack.service;

import com.transittrack.dto.response.DashboardStatsResponse;
import com.transittrack.dto.response.VehicleLocationResponse;

import java.util.List;

/**
 * Service interface aggregating system-wide metrics and status for administrators.
 */
public interface AdminDashboardService {

    DashboardStatsResponse getDashboardStats();

    List<VehicleLocationResponse> getActiveVehiclesLocation();
}

package com.transittrack.dto.request;

import jakarta.validation.constraints.NotNull;

public class AssignRouteRequest {

    @NotNull(message = "Route ID is required")
    private Long routeId;

    public AssignRouteRequest() {
    }

    public AssignRouteRequest(Long routeId) {
        this.routeId = routeId;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }
}

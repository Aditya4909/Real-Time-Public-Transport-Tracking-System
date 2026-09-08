package com.transittrack.service;

import com.transittrack.dto.request.StopCreateRequest;
import com.transittrack.dto.response.PagedResponse;
import com.transittrack.dto.response.StopResponse;

import java.util.List;

/**
 * Service interface managing physical transit stops.
 */
public interface StopService {

    StopResponse createStop(StopCreateRequest request);

    StopResponse getStopById(Long id);

    PagedResponse<StopResponse> getAllStops(int page, int size, String sortBy, String sortDir);

    StopResponse updateStop(Long id, StopCreateRequest request);

    void deleteStop(Long id);

    List<StopResponse> searchStops(String query);

    List<StopResponse> findNearbyStops(Double latitude, Double longitude, Double radiusKm);
}

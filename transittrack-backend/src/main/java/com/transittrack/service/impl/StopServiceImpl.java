package com.transittrack.service.impl;

import com.transittrack.dto.request.StopCreateRequest;
import com.transittrack.dto.response.PagedResponse;
import com.transittrack.dto.response.StopResponse;
import com.transittrack.entity.Stop;
import com.transittrack.exception.BadRequestException;
import com.transittrack.exception.ResourceNotFoundException;
import com.transittrack.repository.StopRepository;
import com.transittrack.service.StopService;
import com.transittrack.util.GeoUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of StopService for CRUD operations and geospatial stop search.
 */
@Service
public class StopServiceImpl implements StopService {

    private final StopRepository stopRepository;

    public StopServiceImpl(StopRepository stopRepository) {
        this.stopRepository = stopRepository;
    }

    @Override
    @Transactional
    public StopResponse createStop(StopCreateRequest request) {
        if (stopRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Stop with code '" + request.getCode() + "' already exists!");
        }

        Stop stop = new Stop(
                request.getCode().toUpperCase().trim(),
                request.getName().trim(),
                request.getLatitude(),
                request.getLongitude(),
                request.getAddress()
        );

        Stop savedStop = stopRepository.save(stop);
        return mapStopToResponse(savedStop);
    }

    @Override
    @Transactional(readOnly = true)
    public StopResponse getStopById(Long id) {
        Stop stop = stopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stop", "id", id));
        return mapStopToResponse(stop);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<StopResponse> getAllStops(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Stop> stops = stopRepository.findAll(pageable);
        List<StopResponse> content = stops.getContent().stream()
                .map(this::mapStopToResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(content, stops.getNumber(), stops.getSize(), stops.getTotalElements(), stops.getTotalPages(), stops.isLast());
    }

    @Override
    @Transactional
    public StopResponse updateStop(Long id, StopCreateRequest request) {
        Stop stop = stopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stop", "id", id));

        if (!stop.getCode().equalsIgnoreCase(request.getCode()) && stopRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Stop code '" + request.getCode() + "' is already in use by another stop");
        }

        stop.setCode(request.getCode().toUpperCase().trim());
        stop.setName(request.getName().trim());
        stop.setLatitude(request.getLatitude());
        stop.setLongitude(request.getLongitude());
        stop.setAddress(request.getAddress());

        Stop updatedStop = stopRepository.save(stop);
        return mapStopToResponse(updatedStop);
    }

    @Override
    @Transactional
    public void deleteStop(Long id) {
        Stop stop = stopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stop", "id", id));
        stopRepository.delete(stop);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StopResponse> searchStops(String query) {
        return stopRepository.searchStops(query).stream()
                .map(this::mapStopToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StopResponse> findNearbyStops(Double latitude, Double longitude, Double radiusKm) {
        double maxRadius = (radiusKm != null && radiusKm > 0) ? radiusKm : 5.0; // Default 5 km radius
        return stopRepository.findAll().stream()
                .filter(stop -> GeoUtils.calculateDistanceKm(latitude, longitude, stop.getLatitude(), stop.getLongitude()) <= maxRadius)
                .map(this::mapStopToResponse)
                .collect(Collectors.toList());
    }

    public StopResponse mapStopToResponse(Stop stop) {
        return new StopResponse(
                stop.getId(),
                stop.getCode(),
                stop.getName(),
                stop.getLatitude(),
                stop.getLongitude(),
                stop.getAddress(),
                stop.getCreatedAt()
        );
    }
}

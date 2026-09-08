package com.transittrack.repository;

import com.transittrack.entity.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for RouteStop junction entity.
 */
@Repository
public interface RouteStopRepository extends JpaRepository<RouteStop, Long> {

    List<RouteStop> findByRouteIdOrderByStopSequenceAsc(Long routeId);

    Optional<RouteStop> findByRouteIdAndStopId(Long routeId, Long stopId);

    Optional<RouteStop> findByRouteIdAndStopSequence(Long routeId, Integer stopSequence);

    void deleteByRouteIdAndStopId(Long routeId, Long stopId);

    long countByRouteId(Long routeId);
}

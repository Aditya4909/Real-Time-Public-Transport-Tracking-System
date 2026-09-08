package com.transittrack.repository;

import com.transittrack.entity.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Route entity.
 */
@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    Optional<Route> findByRouteNumber(String routeNumber);

    boolean existsByRouteNumber(String routeNumber);

    Page<Route> findByActive(boolean active, Pageable pageable);

    @Query("SELECT r FROM Route r WHERE r.active = true AND (" +
            "LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.routeNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.origin) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.destination) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Route> searchRoutes(@Param("keyword") String keyword);

    @Query("SELECT r FROM Route r WHERE r.active = true AND " +
            "LOWER(r.origin) LIKE LOWER(CONCAT('%', :origin, '%')) AND " +
            "LOWER(r.destination) LIKE LOWER(CONCAT('%', :destination, '%'))")
    List<Route> findByOriginAndDestination(@Param("origin") String origin, @Param("destination") String destination);

    long countByActive(boolean active);
}

package com.transittrack.repository;

import com.transittrack.entity.Vehicle;
import com.transittrack.entity.VehicleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Vehicle entity.
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);

    boolean existsByRegistrationNumber(String registrationNumber);

    Page<Vehicle> findByStatus(VehicleStatus status, Pageable pageable);

    List<Vehicle> findByStatus(VehicleStatus status);

    List<Vehicle> findByRouteId(Long routeId);

    List<Vehicle> findByRouteIdAndStatus(Long routeId, VehicleStatus status);

    Optional<Vehicle> findByDriverId(Long driverId);

    long countByStatus(VehicleStatus status);
}

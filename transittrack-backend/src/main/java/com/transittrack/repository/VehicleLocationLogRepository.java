package com.transittrack.repository;

import com.transittrack.entity.VehicleLocationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for VehicleLocationLog entity.
 */
@Repository
public interface VehicleLocationLogRepository extends JpaRepository<VehicleLocationLog, Long> {

    Page<VehicleLocationLog> findByVehicleIdOrderByRecordedAtDesc(Long vehicleId, Pageable pageable);
}

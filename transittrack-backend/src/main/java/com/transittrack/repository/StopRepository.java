package com.transittrack.repository;

import com.transittrack.entity.Stop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Stop entity.
 */
@Repository
public interface StopRepository extends JpaRepository<Stop, Long> {

    Optional<Stop> findByCode(String code);

    boolean existsByCode(String code);

    Page<Stop> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT s FROM Stop s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.code) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Stop> searchStops(@Param("query") String query);
}

package com.transittrack.repository;

import com.transittrack.entity.ERole;
import com.transittrack.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Spring Data JPA repository for Role entity.
 * @Repository registers this interface as a Spring Data repository bean with exception translation.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
}

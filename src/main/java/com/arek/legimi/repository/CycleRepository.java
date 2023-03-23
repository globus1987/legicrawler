package com.arek.legimi.repository;

import com.arek.legimi.domain.Cycle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Cycle entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CycleRepository extends JpaRepository<Cycle, String> {
    Page<Cycle> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
}

package com.arek.legicrawler.repository;

import com.arek.legicrawler.domain.Cycle;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Cycle entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CycleRepository extends JpaRepository<Cycle, String> {
    Page<Cycle> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("select cycle.id from Cycle cycle where  upper(cycle.name) like %:query%")
    List<String> findIdsByName(@Param("query") String query);

    @Query("select id from Cycle")
    List<String> findAllIds();
}

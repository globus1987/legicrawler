package com.arek.legicrawler.repository;

import com.arek.legicrawler.domain.Book;
import com.arek.legicrawler.domain.History;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the History entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HistoryRepository extends JpaRepository<History, String> {
    @EntityGraph(attributePaths = { "data" })
    List<History> findAll();
}

package com.arek.legicrawler.repository;

import com.arek.legicrawler.domain.Author;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Author entity.
 *
 * When extending this class, extend AuthorRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface AuthorRepository extends AuthorRepositoryWithBagRelationships, JpaRepository<Author, String> {
    default Optional<Author> findOneWithEagerRelationships(String id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    Page<Author> findAllByNameContainingIgnoreCase(Pageable pageable, String name);
    List<Author> findAllByNameContainingIgnoreCase(String name);

    default Page<Author> findAllWithEagerRelationships(Pageable pageable, String query) {
        return this.fetchBagRelationships(this.findAllByNameContainingIgnoreCase(pageable, query));
    }

    default Page<Author> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }

    @Query("select author.id from Author author where  upper(author.name) like %:query%")
    List<String> findIdsByName(@Param("query") String query);
}

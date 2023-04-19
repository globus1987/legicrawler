package com.arek.legicrawler.repository;

import com.arek.legicrawler.domain.Author;
import com.arek.legicrawler.domain.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Collection entity.
 *
 * When extending this class, extend CollectionRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface CollectionRepository extends CollectionRepositoryWithBagRelationships, JpaRepository<Collection, String> {
    default Optional<Collection> findOneWithEagerRelationships(String id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    @Query("select id from Collection")
    List<String> findAllIds();

    @Query("select collection from Collection collection JOIN FETCH collection.books where collection.id=:id")
    Optional<Collection> findById(@Param("id") String id);

    default List<Collection> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Collection> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }

    @Query("select collection.id from Collection collection where  upper(collection.name) like %:query%")
    List<String> findIdsByName(@Param("query") String query);

    @EntityGraph(attributePaths = { "books" })
    List<Collection> findAllByIdIn(List<String> ids);
}

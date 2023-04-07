package com.arek.legicrawler.repository;

import com.arek.legicrawler.domain.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class CollectionRepositoryWithBagRelationshipsImpl implements CollectionRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Collection> fetchBagRelationships(Optional<Collection> collection) {
        return collection.map(this::fetchBooks);
    }

    @Override
    public Page<Collection> fetchBagRelationships(Page<Collection> collections) {
        return new PageImpl<>(fetchBagRelationships(collections.getContent()), collections.getPageable(), collections.getTotalElements());
    }

    @Override
    public List<Collection> fetchBagRelationships(List<Collection> collections) {
        return Optional.of(collections).map(this::fetchBooks).orElse(Collections.emptyList());
    }

    Collection fetchBooks(Collection result) {
        return entityManager
            .createQuery(
                "select collection from Collection collection left join fetch collection.books where collection is :collection",
                Collection.class
            )
            .setParameter("collection", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Collection> fetchBooks(List<Collection> collections) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, collections.size()).forEach(index -> order.put(collections.get(index).getId(), index));
        List<Collection> result = entityManager
            .createQuery(
                "select distinct collection from Collection collection left join fetch collection.books where collection in :collections",
                Collection.class
            )
            .setParameter("collections", collections)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}

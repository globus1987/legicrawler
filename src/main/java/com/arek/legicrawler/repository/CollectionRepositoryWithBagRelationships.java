package com.arek.legicrawler.repository;

import com.arek.legicrawler.domain.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface CollectionRepositoryWithBagRelationships {
    Optional<Collection> fetchBagRelationships(Optional<Collection> collection);

    List<Collection> fetchBagRelationships(List<Collection> collections);

    Page<Collection> fetchBagRelationships(Page<Collection> collections);
}

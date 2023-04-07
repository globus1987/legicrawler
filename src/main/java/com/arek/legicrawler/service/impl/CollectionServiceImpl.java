package com.arek.legicrawler.service.impl;

import com.arek.legicrawler.domain.Collection;
import com.arek.legicrawler.repository.CollectionRepository;
import com.arek.legicrawler.service.CollectionService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Collection}.
 */
@Service
@Transactional
public class CollectionServiceImpl implements CollectionService {

    private final Logger log = LoggerFactory.getLogger(CollectionServiceImpl.class);

    private final CollectionRepository collectionRepository;

    public CollectionServiceImpl(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    @Override
    public Collection save(Collection collection) {
        log.debug("Request to save Collection : {}", collection);
        return collectionRepository.save(collection);
    }

    @Override
    public Collection update(Collection collection) {
        log.debug("Request to update Collection : {}", collection);
        collection.setIsPersisted();
        return collectionRepository.save(collection);
    }

    @Override
    public Optional<Collection> partialUpdate(Collection collection) {
        log.debug("Request to partially update Collection : {}", collection);

        return collectionRepository
            .findById(collection.getId())
            .map(existingCollection -> {
                if (collection.getName() != null) {
                    existingCollection.setName(collection.getName());
                }
                if (collection.getUrl() != null) {
                    existingCollection.setUrl(collection.getUrl());
                }

                return existingCollection;
            })
            .map(collectionRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Collection> findAll() {
        log.debug("Request to get all Collections");
        return collectionRepository.findAll();
    }

    public Page<Collection> findAllWithEagerRelationships(Pageable pageable) {
        return collectionRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Collection> findOne(String id) {
        log.debug("Request to get Collection : {}", id);
        return collectionRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(String id) {
        log.debug("Request to delete Collection : {}", id);
        collectionRepository.deleteById(id);
    }
}

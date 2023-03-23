package com.arek.legimi.service.impl;

import com.arek.legimi.domain.Cycle;
import com.arek.legimi.repository.CycleRepository;
import com.arek.legimi.service.CycleService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Cycle}.
 */
@Service
@Transactional
public class CycleServiceImpl implements CycleService {

    private final Logger log = LoggerFactory.getLogger(CycleServiceImpl.class);

    private final CycleRepository cycleRepository;

    public CycleServiceImpl(CycleRepository cycleRepository) {
        this.cycleRepository = cycleRepository;
    }

    @Override
    public Cycle save(Cycle cycle) {
        log.debug("Request to save Cycle : {}", cycle);
        return cycleRepository.save(cycle);
    }

    @Override
    public Cycle update(Cycle cycle) {
        log.debug("Request to update Cycle : {}", cycle);
        cycle.setIsPersisted();
        return cycleRepository.save(cycle);
    }

    @Override
    public Optional<Cycle> partialUpdate(Cycle cycle) {
        log.debug("Request to partially update Cycle : {}", cycle);

        return cycleRepository
            .findById(cycle.getId())
            .map(existingCycle -> {
                if (cycle.getName() != null) {
                    existingCycle.setName(cycle.getName());
                }
                if (cycle.getUrl() != null) {
                    existingCycle.setUrl(cycle.getUrl());
                }

                return existingCycle;
            })
            .map(cycleRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Cycle> findAll(Pageable pageable) {
        log.debug("Request to get all Cycles");
        return cycleRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Cycle> findAll(Pageable pageable, String query) {
        log.debug("Request to get all Cycles");
        return cycleRepository.findAllByNameContainingIgnoreCase(query, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cycle> findOne(String id) {
        log.debug("Request to get Cycle : {}", id);
        return cycleRepository.findById(id);
    }

    @Override
    public void delete(String id) {
        log.debug("Request to delete Cycle : {}", id);
        cycleRepository.deleteById(id);
    }
}

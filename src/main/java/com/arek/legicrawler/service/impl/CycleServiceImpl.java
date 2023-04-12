package com.arek.legicrawler.service.impl;

import com.arek.legicrawler.domain.Cycle;
import com.arek.legicrawler.repository.CycleRepository;
import com.arek.legicrawler.service.CycleService;
import java.util.ArrayList;
import java.util.List;
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
    @Transactional(readOnly = true)
    public List<String> findIdList(String query) {
        log.debug("Request to get all Authors");
        if (query.isEmpty()) return new ArrayList<>();
        return cycleRepository.findIdsByName(query.toUpperCase());
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
        Optional<Cycle> byId = cycleRepository.findById(id);
        var cyc = byId.get();
        return byId;
    }

    @Override
    public void delete(String id) {
        log.debug("Request to delete Cycle : {}", id);
        cycleRepository.deleteById(id);
    }

    @Override
    public List<String> findAllIds() {
        return cycleRepository.findAllIds();
    }
}

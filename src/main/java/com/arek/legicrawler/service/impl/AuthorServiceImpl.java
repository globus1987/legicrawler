package com.arek.legicrawler.service.impl;

import com.arek.legicrawler.domain.Author;
import com.arek.legicrawler.repository.AuthorRepository;
import com.arek.legicrawler.service.AuthorService;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Author}.
 */
@Service
@Transactional
public class AuthorServiceImpl implements AuthorService {

    private final Logger log = LoggerFactory.getLogger(AuthorServiceImpl.class);

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public Author save(Author author) {
        log.debug("Request to save Author : {}", author);
        return authorRepository.save(author);
    }

    @Override
    public List<Author> saveAll(List<Author> authors) {
        log.debug("Request to save Authors : {}", authors);
        return authorRepository.saveAll(authors);
    }

    @Override
    public Author update(Author author) {
        log.debug("Request to update Author : {}", author);
        author.setIsPersisted();
        return authorRepository.save(author);
    }

    @Override
    public Optional<Author> partialUpdate(Author author) {
        log.debug("Request to partially update Author : {}", author);

        return authorRepository
            .findById(author.getId())
            .map(existingAuthor -> {
                if (author.getName() != null) {
                    existingAuthor.setName(author.getName());
                }
                if (author.getUrl() != null) {
                    existingAuthor.setUrl(author.getUrl());
                }

                return existingAuthor;
            })
            .map(authorRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Author> findAll(Pageable pageable, String query) {
        log.debug("Request to get all Authors");
        return authorRepository.findAllByNameContainingIgnoreCase(pageable, query);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Author> findAll(String query) {
        log.debug("Request to get all Authors");
        if (query.isEmpty()) return new ArrayList<>();
        return authorRepository.findAllByNameContainingIgnoreCase(query);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findIdList(String query) {
        log.debug("Request to get all Authors");
        if (query.isEmpty()) return new ArrayList<>();
        return authorRepository.findIdsByName(query.toUpperCase());
    }

    public Page<Author> findAllWithEagerRelationships(Pageable pageable, String query) {
        return authorRepository.findAllWithEagerRelationships(pageable, query);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Author> findOne(String id) {
        log.debug("Request to get Author : {}", id);
        return authorRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(String id) {
        log.debug("Request to delete Author : {}", id);
        authorRepository.deleteById(id);
    }

    @Override
    public List<String> findAllIds() {
        return authorRepository.findAllIds();
    }
}

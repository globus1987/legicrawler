package com.arek.legicrawler.service.impl;

import com.arek.legicrawler.domain.Author;
import com.arek.legicrawler.domain.Book;
import com.arek.legicrawler.domain.Cycle;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> filterByTitle(String title) {
        return (root, query, builder) -> builder.like(builder.upper(root.get("title")), "%" + title.toUpperCase() + "%");
    }

    public static Specification<Book> filterByAdded(LocalDate added) {
        return (root, query, builder) -> builder.equal(root.get("added"), added);
    }

    public static Specification<Book> filterByAuthors(List<String> authors) {
        return (root, query, builder) -> {
            Join<Book, Author> join = root.join("authors");
            return join.get("id").in(authors);
        };
    }

    public static Specification<Book> filterByCycles(List<String> cycles) {
        return (root, query, builder) -> {
            Join<Book, Cycle> join = root.join("cycle");
            return join.get("id").in(cycles);
        };
    }
}

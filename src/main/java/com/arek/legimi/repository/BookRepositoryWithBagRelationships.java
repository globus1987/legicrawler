package com.arek.legimi.repository;

import com.arek.legimi.domain.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface BookRepositoryWithBagRelationships {
    Optional<Book> fetchBagRelationships(Optional<Book> book);

    List<Book> fetchBagRelationships(List<Book> books);

    Page<Book> fetchBagRelationships(Page<Book> books);
}

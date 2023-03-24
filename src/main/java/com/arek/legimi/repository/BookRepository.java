package com.arek.legimi.repository;

import com.arek.legimi.domain.Book;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Book entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    @Query("select id from Book")
    List<String> findAllIds();
}

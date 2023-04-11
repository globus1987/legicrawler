package com.arek.legicrawler.repository;

import com.arek.legicrawler.domain.Author;
import com.arek.legicrawler.domain.Book;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Book entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookRepository extends JpaRepository<Book, String>, JpaSpecificationExecutor<Book> {
    @Query("select id from Book")
    List<String> findAllIds();

    @Query("select id from Book where cycle is null")
    List<String> findAllIdsWithNullCycle();

    Page<Book> findAllByTitleContainingIgnoreCase(Pageable pageable, String title);

    @Query("SELECT DISTINCT book from Book book JOIN book.authors author where author in :authors")
    Page<Book> findAllByAuthors(Pageable pageable, @Param("authors") List<Author> authors);

    @Query("SELECT DISTINCT book from Book book JOIN book.authors author where author in :authors and book.title like %:title%")
    Page<Book> findAllByTitleContainingIgnoreCaseAndAuthorsContainingIgnoreCase(
        Pageable pageable,
        @Param("authors") List<Author> authors,
        @Param("title") String title
    );

    Page<Book> findAll(Specification<Book> spec, Pageable pageable);

    boolean existsByIdAndCycleIsNull(String id);
    boolean existsByIdAndCycleIsNotNull(String id);
}

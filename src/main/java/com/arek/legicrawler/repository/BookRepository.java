package com.arek.legicrawler.repository;

import com.arek.legicrawler.domain.Author;
import com.arek.legicrawler.domain.Book;
import com.arek.legicrawler.domain.Statistics;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Book entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookRepository extends BookRepositoryWithBagRelationships, JpaRepository<Book, String>, JpaSpecificationExecutor<Book> {
    @Query("select id from Book")
    List<String> findAllIds();

    @Query("SELECT new com.arek.legicrawler.domain.Statistics(b.added, COUNT(b)) FROM Book b GROUP BY b.added")
    List<Statistics> countBooksByDay();

    @EntityGraph(attributePaths = { "collections", "authors", "cycle" })
    @Query("select book from Book book where book.id=:id")
    Optional<Book> findById(@Param("id") String id);

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

    @EntityGraph(attributePaths = { "collections", "authors", "cycle" })
    List<Book> findAll(@Nullable Specification<Book> spec, Sort sort);

    boolean existsByIdAndCycleIsNull(String id);
    boolean existsByIdAndCycleIsNotNull(String id);

    default Optional<Book> findOneWithEagerRelationships(String id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Book> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }
}

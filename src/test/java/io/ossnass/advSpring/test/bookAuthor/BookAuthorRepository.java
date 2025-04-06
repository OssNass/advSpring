package io.ossnass.advSpring.test.bookAuthor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookAuthorRepository extends JpaRepository<BookAuthor, BookAuthorId> {
    @Query("SELECT bookAuthor from BookAuthor bookAuthor where bookAuthor.authorId=:authorId")
    List<BookAuthor> findByAuthorId(@Param("authorId") Integer authorId);
}

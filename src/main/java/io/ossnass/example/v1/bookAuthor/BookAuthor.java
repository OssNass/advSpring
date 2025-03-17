package io.ossnass.example.v1.bookAuthor;

import io.ossnass.advSpring.Deletable;
import io.ossnass.example.v1.author.Author;
import io.ossnass.example.v1.book.Book;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "book_author")
@Getter
@Setter
@Accessors(chain = true)
@IdClass(BookAuthorId.class)
public class BookAuthor extends Deletable {
    @Id
    @Column(name = "book_id",nullable = false)
    private Integer bookId;
    @Id
    @Column(name = "author_id",nullable = false)
    private Integer authorId;

    @ManyToOne(targetEntity = Book.class)
    @JoinColumn(name = "book_id",insertable = false,updatable = false,referencedColumnName = "id")
    private Book book;

    @ManyToOne(targetEntity = Author.class)
    @JoinColumn(name = "author_id",insertable = false,updatable = false,referencedColumnName = "id")
    private Author author;
}

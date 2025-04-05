package io.ossnass.advSpring.test.book;

import io.ossnass.advSpring.Deletable;
import io.ossnass.advSpring.test.bookAuthor.BookAuthor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Entity
@Table(name = "books")
@Accessors(chain = true)
@Getter
@Setter
public class Book extends Deletable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String title;

    @OneToMany(targetEntity = BookAuthor.class, mappedBy = "bookId")
    private List<BookAuthor> authors;
}

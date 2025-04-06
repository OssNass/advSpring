package io.ossnass.advSpring.test.author;

import io.ossnass.advSpring.Deletable;
import io.ossnass.advSpring.test.bookAuthor.BookAuthor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Entity
@Table(name = "authors")
@Getter
@Setter
@Accessors(chain = true)
public class Author extends Deletable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(targetEntity = BookAuthor.class, mappedBy = "authorId")
    private List<BookAuthor> books;
}

package io.ossnass.example.v1.book;

import io.ossnass.advSpring.Deletable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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

    @Column(name = "title", nullable = false, length = 255)
    private String title;
}

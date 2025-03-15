package io.ossnass.example.v1.author;

import io.ossnass.advSpring.Deletable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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

    @Column(name = "name", length = 255, nullable = false)
    private String name;
}

package io.ossnass.advSpring.test.bookAuthor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class BookAuthorId implements Serializable {
    private Integer bookId;
    private Integer authorId;
}

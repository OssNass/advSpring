package io.ossnass.example.v1.author;

import io.ossnass.example.v1.book.SimpleBookDto;

public record AuthorDto(Integer id, String title, SimpleBookDto book) {
}

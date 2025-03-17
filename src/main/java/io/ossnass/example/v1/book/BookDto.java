package io.ossnass.example.v1.book;

import io.ossnass.example.v1.author.SimpleAuthorDto;

public record BookDto(Integer id, String title, SimpleAuthorDto author) {
}

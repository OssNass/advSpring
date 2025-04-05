package io.ossnass.advSpring.test.book;

import io.ossnass.advSpring.test.author.SimpleAuthorDto;

import java.util.List;

public record BookDto(Integer id, String title, List<SimpleAuthorDto> authors) {
}

package io.ossnass.advSpring.test.author;

import io.ossnass.advSpring.test.book.SimpleBookDto;

import java.util.List;

public record AuthorDto(Integer id, String name, List<SimpleBookDto> books) {
}

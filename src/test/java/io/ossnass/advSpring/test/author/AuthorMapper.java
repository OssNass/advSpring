package io.ossnass.advSpring.test.author;

import io.ossnass.advSpring.DtoMapper;
import io.ossnass.advSpring.test.book.SimpleBookDto;
import io.ossnass.advSpring.test.bookAuthor.BookAuthor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface AuthorMapper extends DtoMapper<Author, AuthorDto> {

    @Mapping(target = "books", expression = "java(toSimpleBookDto(entity.getBooks()))")
    AuthorDto fromEntity(Author entity);

    @Mapping(target = "books", expression = "java(toBooks(dto.id(),dto.books()))")
    Author fromDto(AuthorDto dto);

    default List<SimpleBookDto> toSimpleBookDto(List<BookAuthor> books) {
        return books == null ? null : books.stream().map(item -> new SimpleBookDto(item.getAuthor().getId(), item.getAuthor().getName())).collect(Collectors.toList());
    }

    default List<BookAuthor> toBooks(Integer id, List<SimpleBookDto> simpleBookDtoList) {
        return simpleBookDtoList == null ? null : simpleBookDtoList.stream().map(item -> new BookAuthor().setBookId(id).setAuthorId(item.id())).collect(Collectors.toList());

    }
}

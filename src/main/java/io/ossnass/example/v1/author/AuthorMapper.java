package io.ossnass.example.v1.author;

import io.ossnass.advSpring.DtoMapper;
import io.ossnass.example.v1.book.SimpleBookDto;
import io.ossnass.example.v1.bookAuthor.BookAuthor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface AuthorMapper extends DtoMapper<Author, AuthorDto> {

    @Mapping(target = "author", source = "java(toSimpleAuthorDto(entity.getAuthor())")
    AuthorDto fromEntity(Author entity);

    @Mapping(target = "author", source = "java(toBooks(dto.id(),dto.author())")
    Author fromDto(AuthorDto dto);

    default List<SimpleBookDto> toSimpleAuthorDto(List<BookAuthor> books) {
        return books.stream().map(item -> new SimpleBookDto(item.getAuthor().getId(), item.getAuthor().getName())).collect(Collectors.toList());
    }

    default List<BookAuthor> toBooks(Integer id, List<SimpleBookDto> simpleBookDtoList) {
        return simpleBookDtoList.stream().map(item -> new BookAuthor().setBookId(id).setAuthorId(item.id())).collect(Collectors.toList());
    }
}

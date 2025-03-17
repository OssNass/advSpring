package io.ossnass.example.v1.book;

import io.ossnass.advSpring.DtoMapper;
import io.ossnass.example.v1.author.SimpleAuthorDto;
import io.ossnass.example.v1.bookAuthor.BookAuthor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface BookMapper extends DtoMapper<Book, BookDto> {


    @Mapping(target = "author", source = "java(toSimpleAuthorDto(entity.getAuthor())")
    BookDto fromEntity(Book entity);

    @Mapping(target = "author", source = "java(toAuthor(dto.id(),dto.author())")
    Book fromDto(BookDto dto);

    default List<SimpleAuthorDto> toSimpleAuthorDto(List<BookAuthor> authors) {
        return authors.stream().map(item -> new SimpleAuthorDto(item.getAuthor().getId(), item.getAuthor().getName())).collect(Collectors.toList());
    }

    default List<BookAuthor> toAuthor(Integer id, List<SimpleAuthorDto> simpleAuthorDtoList) {
        return simpleAuthorDtoList.stream().map(item -> new BookAuthor().setBookId(id).setAuthorId(item.id())).collect(Collectors.toList());
    }
}

package io.ossnass.advSpring.test.book;

import io.ossnass.advSpring.DtoMapper;
import io.ossnass.advSpring.test.author.SimpleAuthorDto;
import io.ossnass.advSpring.test.bookAuthor.BookAuthor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface BookMapper extends DtoMapper<Book, BookDto> {


    @Mapping(target = "authors", expression = "java(toSimpleAuthorDto(entity.getAuthors()))")
    BookDto fromEntity(Book entity);

    @Mapping(target = "authors", expression = "java(toAuthor(dto.id(),dto.authors()))")
    Book fromDto(BookDto dto);

    default List<SimpleAuthorDto> toSimpleAuthorDto(List<BookAuthor> authors) {
        return authors == null ? null : authors.stream().map(item -> new SimpleAuthorDto(item.getAuthor().getId(), item.getAuthor().getName())).collect(Collectors.toList());
    }

    default List<BookAuthor> toAuthor(Integer id, List<SimpleAuthorDto> simpleAuthorDtoList) {
        return simpleAuthorDtoList == null ? null : simpleAuthorDtoList.stream().map(item -> new BookAuthor().setBookId(id).setAuthorId(item.id())).collect(Collectors.toList());
    }
}

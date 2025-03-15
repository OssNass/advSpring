package io.ossnass.example.v1.book;

import io.ossnass.advSpring.DtoMapper;
import org.mapstruct.Mapper;

@Mapper
public interface BookMapper extends DtoMapper<Book,BookDto> {
}

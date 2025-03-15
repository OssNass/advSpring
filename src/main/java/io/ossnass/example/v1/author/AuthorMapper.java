package io.ossnass.example.v1.author;

import io.ossnass.advSpring.DtoMapper;
import org.mapstruct.Mapper;

@Mapper
public interface AuthorMapper extends DtoMapper<Author,AuthorDto> {
}

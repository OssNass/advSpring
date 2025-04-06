package io.ossnass.advSpring.test.author;

import io.ossnass.advSpring.DtoMapper;
import org.mapstruct.Mapper;

@Mapper
public interface SimpleAuthorMapper extends DtoMapper<Author, SimpleAuthorDto> {


}

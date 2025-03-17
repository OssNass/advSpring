package io.ossnass.example.v1.author;

import io.ossnass.advSpring.DtoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper
public interface SimpleAuthorMapper extends DtoMapper<Author, SimpleAuthorDto> {


}

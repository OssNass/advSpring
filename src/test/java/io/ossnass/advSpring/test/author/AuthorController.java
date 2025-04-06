package io.ossnass.advSpring.test.author;

import io.ossnass.advSpring.CRUDController;
import io.ossnass.advSpring.CRUDService;
import io.ossnass.advSpring.annotations.ControllerInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerInfo(mapper = AuthorMapper.class)
@RequestMapping("/api/v1/authors")
public class AuthorController extends CRUDController<Author, Integer, AuthorDto> {
    public AuthorController(CRUDService<Author, Integer> service) {
        super(service);
    }
}

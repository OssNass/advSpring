package io.ossnass.advSpring.test.book;

import io.ossnass.advSpring.CRUDController;
import io.ossnass.advSpring.CRUDService;
import io.ossnass.advSpring.annotations.ControllerInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ControllerInfo(mapper = BookMapper.class)
@RestController
@RequestMapping("/api/v1/books")
public class BookController extends CRUDController<Book, Integer, BookDto> {

    public BookController(CRUDService<Book, Integer> service) {
        super(service);
    }
}

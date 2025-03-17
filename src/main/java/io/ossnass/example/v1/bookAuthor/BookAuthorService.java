package io.ossnass.example.v1.bookAuthor;

import io.ossnass.advSpring.CRUDService;
import io.ossnass.advSpring.FilterAndSortInfoService;
import io.ossnass.advSpring.JinqStreamService;
import io.ossnass.advSpring.annotations.ServiceInfo;
import jakarta.persistence.EntityManager;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Service;

@Service
@ServiceInfo(id = "bookAuthor", entityClass = BookAuthor.class)
public class BookAuthorService extends CRUDService<BookAuthor, BookAuthorId> {
    public BookAuthorService(FilterAndSortInfoService filterService, BookAuthorRepository repository, EntityManager em, SearchSession searchSession, JinqStreamService streamService) {
        super(filterService, repository, em, searchSession, streamService);
    }

    @Override
    protected BookAuthor processUpdate(BookAuthor dbObject, BookAuthor requestEntity) {
        return null;
    }

    @Override
    protected BookAuthorId partsToIdClass(String[] idParts) {
        return new BookAuthorId(Integer.parseInt(idParts[0]), Integer.parseInt(idParts[1]));
    }

    @Override
    protected int idFieldCount() {
        return 2;
    }
}

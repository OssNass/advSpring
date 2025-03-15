package io.ossnass.example.v1.book;

import io.ossnass.advSpring.CRUDService;
import io.ossnass.advSpring.FilterAndSortInfoService;
import io.ossnass.advSpring.JinqStreamService;
import io.ossnass.advSpring.annotations.ServiceInfo;
import jakarta.persistence.EntityManager;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@ServiceInfo(id = "book", entityClass = Book.class)
@Service
public class BookService extends CRUDService<Book, Integer> {
    public BookService(FilterAndSortInfoService filterService,
                       JpaRepository<Book, Integer> repository,
                       EntityManager em,
                       SearchSession searchSession,
                       JinqStreamService streamService) {
        super(filterService, repository, em, searchSession, streamService);
    }

    @Override
    protected Book processUpdate(Book dbObject, Book requestEntity) {
        return dbObject.setTitle(requestEntity.getTitle());
    }

    @Override
    protected Integer partsToIdClass(String[] idParts) {
        return Integer.parseInt(idParts[0]);
    }

    @Override
    protected int idFieldCount() {
        return 1;
    }
}

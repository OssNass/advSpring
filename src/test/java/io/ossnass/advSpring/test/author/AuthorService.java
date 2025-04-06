package io.ossnass.advSpring.test.author;

import io.ossnass.advSpring.CRUDService;
import io.ossnass.advSpring.FilterAndSortInfoService;
import io.ossnass.advSpring.JinqStreamService;
import io.ossnass.advSpring.annotations.ServiceInfo;
import io.ossnass.advSpring.annotations.hooks.PreDelete;
import io.ossnass.advSpring.test.bookAuthor.BookAuthorRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@ServiceInfo(id = "author", entityClass = Author.class)
public class AuthorService extends CRUDService<Author, Integer> {
    private final BookAuthorRepository bookAuthorRepository;

    public AuthorService(FilterAndSortInfoService filterService,
                         JpaRepository<Author, Integer> repository,
                         EntityManager em,
                         SearchSession searchSession,
                         JinqStreamService streamService, BookAuthorRepository bookAuthorRepository) {
        super(filterService, repository, em, searchSession, streamService);
        this.bookAuthorRepository = bookAuthorRepository;
    }

    @Override
    protected Author processUpdate(Author dbObject, Author requestEntity) {
        return dbObject.setName(requestEntity.getName());
    }

    @PreDelete
    public void preDelete(Author entity) {
        var books = bookAuthorRepository.findByAuthorId(entity.getId());
        if (books != null && !books.isEmpty()) {
            bookAuthorRepository.deleteAll(books);
            bookAuthorRepository.flush();
        }
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

package io.ossnass.advSpring.test.book;

import io.ossnass.advSpring.CRUDService;
import io.ossnass.advSpring.FilterAndSortInfoService;
import io.ossnass.advSpring.JinqStreamService;
import io.ossnass.advSpring.annotations.ServiceInfo;
import io.ossnass.advSpring.annotations.hooks.PostAdd;
import io.ossnass.advSpring.annotations.hooks.PreAdd;
import io.ossnass.advSpring.test.author.AuthorRepository;
import io.ossnass.advSpring.test.bookAuthor.BookAuthor;
import io.ossnass.advSpring.test.bookAuthor.BookAuthorService;
import jakarta.persistence.EntityManager;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@ServiceInfo(id = "book", entityClass = Book.class)
@Service
public class BookService extends CRUDService<Book, Integer> {
    private final BookAuthorService bookAuthorService;
    private final AuthorRepository authorRepository;

    public BookService(FilterAndSortInfoService filterService,
                       JpaRepository<Book, Integer> repository,
                       EntityManager em,
                       SearchSession searchSession,
                       JinqStreamService streamService, BookAuthorService bookAuthorService, AuthorRepository authorRepository) {
        super(filterService, repository, em, searchSession, streamService);
        this.bookAuthorService = bookAuthorService;
        this.authorRepository = authorRepository;
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
    protected Integer idFieldCount() {
        return 1;
    }

    @PreAdd
    public Object preAdd(Book book) {
        var authors = book.getAuthors();
        book.setAuthors(null);
        return authors;
    }

    @PostAdd
    public void postAdd(Book book, Object extra) {
        if (extra != null) {
            var authors = (List<BookAuthor>) extra;
            book.setAuthors(authors.stream().map(author -> bookAuthorService.save(author.setBookId(book.getId()))
                    .setAuthor(authorRepository.findById(author.getAuthorId()).get())).toList());
        }
    }


}

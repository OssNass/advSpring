package io.ossnass.advSpring.test.book.filters;

import io.ossnass.advSpring.JinqStreamService;
import io.ossnass.advSpring.annotations.FilterInfo;
import io.ossnass.advSpring.operators.Filter;
import io.ossnass.advSpring.operators.Operation;
import io.ossnass.advSpring.test.book.Book;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.jinq.jpa.JPAJinqStream;

@FilterInfo(serviceId = "book", operation = Operation.EQUALS, fieldName = "title")
public class TitleFilter extends Filter<Book> {
    public TitleFilter(SearchSession searchSession, JinqStreamService streamService) {
        super(searchSession, streamService);
    }

    @Override
    public JPAJinqStream<Book> addFilter(JPAJinqStream<Book> stream, String value) {
        return stream.where(item -> item.getTitle().contains(value));
    }
}

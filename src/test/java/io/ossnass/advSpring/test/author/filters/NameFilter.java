package io.ossnass.advSpring.test.author.filters;

import io.ossnass.advSpring.JinqStreamService;
import io.ossnass.advSpring.annotations.FilterInfo;
import io.ossnass.advSpring.operators.Filter;
import io.ossnass.advSpring.operators.Operation;
import io.ossnass.advSpring.test.author.Author;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.jinq.jpa.JPAJinqStream;

@FilterInfo(fieldName = "name", operation = Operation.EQUALS, serviceId = "author")
public class NameFilter extends Filter<Author> {
    public NameFilter(SearchSession searchSession, JinqStreamService streamService) {
        super(searchSession, streamService);
    }

    @Override
    public JPAJinqStream<Author> addFilter(JPAJinqStream<Author> stream, String value) {
        return stream.where(item -> item.getName().contains(value));
    }
}

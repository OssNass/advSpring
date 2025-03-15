package io.ossnass.example.v1.author;

import io.ossnass.advSpring.CRUDService;
import io.ossnass.advSpring.FilterAndSortInfoService;
import io.ossnass.advSpring.JinqStreamService;
import io.ossnass.advSpring.annotations.ServiceInfo;
import jakarta.persistence.EntityManager;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@ServiceInfo(id = "author", entityClass = Author.class)
public class AuthorService extends CRUDService<Author, Integer> {
    public AuthorService(FilterAndSortInfoService filterService,
                         JpaRepository<Author, Integer> repository,
                         EntityManager em,
                         SearchSession searchSession,
                         JinqStreamService streamService) {
        super(filterService, repository, em, searchSession, streamService);
    }

    @Override
    protected Author processUpdate(Author dbObject, Author requestEntity) {
        return dbObject.setName(requestEntity.getName());
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

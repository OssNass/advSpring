package io.ossnass.advSpring;

import jakarta.persistence.EntityManager;
import org.jinq.jpa.JPAJinqStream;
import org.jinq.jpa.JinqJPAStreamProvider;
import org.springframework.stereotype.Service;

/**
 * This class is used to create a JINQ jpa stream
 */
@Service
public class JinqStreamService {

    private final JinqJPAStreamProvider provider;

    private final EntityManager em;

    public JinqStreamService(EntityManager em) {
        this.em = em;
        provider = new JinqJPAStreamProvider(em.getEntityManagerFactory());
    }

    /**
     * Creates a custom stream
     *
     * @param entityClass the class of the entity
     * @param <T>         the type of the entity
     * @return the stream
     */
    public <T> JPAJinqStream<T> createCustomStream(Class<T> entityClass) {
        return provider.streamAll(em, entityClass);
    }
}

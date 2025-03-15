package io.ossnass.advSpring;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.jinq.jpa.JinqJPAStreamProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Providers {
    @PersistenceContext
    private EntityManager em;

    private SearchSession session;
    private JinqJPAStreamProvider provider;

    @Bean
    public JinqJPAStreamProvider getJinqProvider() {
        if (provider == null)
            provider = new JinqJPAStreamProvider(em.getEntityManagerFactory());
        return provider;
    }

    @Bean
    public SearchSession getSerchSession() {
        if (session == null)
            session = Search.session(em);
        return session;
    }
}

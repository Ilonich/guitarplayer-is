package ru.ilonich.igps.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class JpaUtil {

    @PersistenceContext
    private EntityManager entityManager;

    public JpaUtil() {}

    public void clear2ndLevelHibernateCache() {
        Session s = (Session) entityManager.getDelegate();
        SessionFactory sf = s.getSessionFactory();
        sf.getCache().evictQueryRegions();
        sf.getCache().evictDefaultQueryRegion();
        sf.getCache().evictCollectionRegions();
        sf.getCache().evictEntityRegions();
    }
}

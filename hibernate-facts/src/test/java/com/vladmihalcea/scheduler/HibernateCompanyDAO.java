package com.vladmihalcea.scheduler;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * HibernateUserRatingDAO -
 *
 * @author Vlad Mihalcea
 */
@Repository
public class HibernateCompanyDAO implements CompanyDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<String> getNames() {
        return entityManager.createQuery("select name from Company").getResultList();
    }
}
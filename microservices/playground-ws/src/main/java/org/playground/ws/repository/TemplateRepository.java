package org.playground.ws.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.playground.ws.dao.TemplateDao;
import org.playground.ws.dao.Post_;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TemplateRepository {

    @PersistenceContext
    EntityManager entityManager;

    public List<TemplateDao> findAll() {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        // create query
        CriteriaQuery<TemplateDao> query = cb.createQuery(TemplateDao.class);
        // set the root class
        Root<TemplateDao> root = query.from(TemplateDao.class);
        //perform query
        return this.entityManager.createQuery(query).getResultList();
    }

    public Optional<TemplateDao> findById(String id) {
        TemplateDao template = null;
        try {
            template = this.entityManager.find(TemplateDao.class, id);
        } catch (NoResultException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(template);
    }

    @Transactional
    public TemplateDao save(TemplateDao template) {
        if (template.getId() == null) {
            this.entityManager.persist(template);
            return template;
        } else {
            return this.entityManager.merge(template);
        }
    }

    @Transactional
    public int deleteById(String id) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        // create delete
        CriteriaDelete<TemplateDao> delete = cb.createCriteriaDelete(TemplateDao.class);
        // set the root class
        Root<TemplateDao> root = delete.from(TemplateDao.class);
        // set where clause
        delete.where(cb.equal(root.get(Post_.id), id));
        // perform update
        return this.entityManager.createQuery(delete).executeUpdate();
    }
}
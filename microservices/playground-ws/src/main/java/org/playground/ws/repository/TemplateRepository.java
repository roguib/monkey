package org.playground.ws.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.playground.ws.dao.Template;
import org.playground.ws.dao.Post_;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TemplateRepository {

    @PersistenceContext
    EntityManager entityManager;

    public List<Template> findAll() {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        // create query
        CriteriaQuery<Template> query = cb.createQuery(Template.class);
        // set the root class
        Root<Template> root = query.from(Template.class);
        //perform query
        return this.entityManager.createQuery(query).getResultList();
    }

    public Optional<Template> findById(String id) {
        Template template = null;
        try {
            template = this.entityManager.find(Template.class, id);
        } catch (NoResultException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(template);
    }

    @Transactional
    public Template save(Template template) {
        if (template.getId() == null) {
            this.entityManager.persist(template);
            return template;
        } else {
            return this.entityManager.merge(template);
        }
    }

    //@Transactional
    //public int updateStatus(String id, Template.Status status) {
    //    CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
    //    // create update
    //    CriteriaUpdate<Template> delete = cb.createCriteriaUpdate(Template.class);
    //    // set the root class
    //    Root<Template> root = delete.from(Template.class);
    //    // set where clause
    //    delete.set(root.get(Post_.status), status);
    //    delete.where(cb.equal(root.get(Post_.id), id));
    //    // perform update
    //    return this.entityManager.createQuery(delete).executeUpdate();
    //}


    @Transactional
    public int deleteById(String id) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        // create delete
        CriteriaDelete<Template> delete = cb.createCriteriaDelete(Template.class);
        // set the root class
        Root<Template> root = delete.from(Template.class);
        // set where clause
        delete.where(cb.equal(root.get(Post_.id), id));
        // perform update
        return this.entityManager.createQuery(delete).executeUpdate();
    }
}
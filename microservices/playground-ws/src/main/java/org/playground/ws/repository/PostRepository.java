package org.playground.ws.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.playground.ws.dao.Post;
import org.playground.ws.dao.Post_;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PostRepository {

    @PersistenceContext
    EntityManager entityManager;

    public List<Post> findAll() {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        // create query
        CriteriaQuery<Post> query = cb.createQuery(Post.class);
        // set the root class
        Root<Post> root = query.from(Post.class);
        //perform query
        return this.entityManager.createQuery(query).getResultList();
    }

    public Optional<Post> findById(String id) {
        Post post = null;
        try {
            post = this.entityManager.find(Post.class, id);
        } catch (NoResultException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(post);
    }

    @Transactional
    public Post save(Post post) {
        if (post.getId() == null) {
            this.entityManager.persist(post);
            return post;
        } else {
            return this.entityManager.merge(post);
        }
    }

    //@Transactional
    //public int updateStatus(String id, Post.Status status) {
    //    CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
    //    // create update
    //    CriteriaUpdate<Post> delete = cb.createCriteriaUpdate(Post.class);
    //    // set the root class
    //    Root<Post> root = delete.from(Post.class);
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
        CriteriaDelete<Post> delete = cb.createCriteriaDelete(Post.class);
        // set the root class
        Root<Post> root = delete.from(Post.class);
        // set where clause
        delete.where(cb.equal(root.get(Post_.id), id));
        // perform update
        return this.entityManager.createQuery(delete).executeUpdate();
    }
}
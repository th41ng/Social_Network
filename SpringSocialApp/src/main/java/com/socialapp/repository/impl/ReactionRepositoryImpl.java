/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.repository.impl;

import com.socialapp.pojo.Reaction;
import com.socialapp.repository.ReactionRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author DELL G15
 */
@Repository
@Transactional
public class ReactionRepositoryImpl implements ReactionRepository {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Reaction> getReactions(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Reaction> q = b.createQuery(Reaction.class);
        Root<Reaction> root = q.from(Reaction.class);
        q.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();

            String type = params.get("type");
            if (type != null && !type.isEmpty()) {
                predicates.add(b.equal(root.get("reactionType"), type));
            }

            String postId = params.get("postId");
            if (postId != null && !postId.isEmpty()) {
                predicates.add(b.equal(root.get("postId").get("postId"), Integer.parseInt(postId)));
            }

            q.where(predicates.toArray(Predicate[]::new));
        }

        Query query = s.createQuery(q);

        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            query.setMaxResults(PAGE_SIZE);
            query.setFirstResult((page - 1) * PAGE_SIZE);
        }

        return query.getResultList();
    }

    @Override
    public Reaction getReactionById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Reaction.class, id);
    }

    @Override
    public Reaction addOrUpdateReaction(Reaction reaction) {
        Session s = this.factory.getObject().getCurrentSession();
        if (reaction.getReactionId() == null) {
            s.persist(reaction);
        } else {
            s.merge(reaction);
        }
        return reaction;
    }

    @Override
    public void deleteReaction(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        Reaction reaction = this.getReactionById(id);
        if (reaction != null) {
            s.remove(reaction); // Xóa cứng
        }
    }

    @Override
    public List<Reaction> getReactionsByPostId(int postId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Reaction> q = b.createQuery(Reaction.class);
        Root<Reaction> root = q.from(Reaction.class);
        q.select(root);

        q.where(b.equal(root.get("postId").get("postId"), postId));

        Query query = s.createQuery(q);
        return query.getResultList();
    }

    @Override
    public List<Reaction> getReactionsByCommentId(int commentId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Reaction> q = b.createQuery(Reaction.class);
        Root<Reaction> root = q.from(Reaction.class);
        q.select(root);

        q.where(b.equal(root.get("commentId").get("commentId"), commentId));

        Query query = s.createQuery(q);
        return query.getResultList();
    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.repository.impl;

import com.socialapp.pojo.Comment;
import com.socialapp.repository.CommentRepository;
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
public class CommentRepositoryImpl implements CommentRepository {

    private static final int PAGE_SIZE = 5;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Comment> getComments(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Comment> q = b.createQuery(Comment.class);
        Root<Comment> root = q.from(Comment.class);
        q.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();

            String content = params.get("content");
            if (content != null && !content.isEmpty()) {
                predicates.add(b.like(root.get("content"), String.format("%%%s%%", content)));
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
    public Comment getCommentById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Comment.class, id);
    }

    @Override
    public Comment addOrUpdateComment(Comment comment) {
        Session s = this.factory.getObject().getCurrentSession();
        if (comment.getCommentId() == null) {
            s.persist(comment);
        } else {
            s.merge(comment);
        }
        return comment;
    }

    @Override
    public void deleteComment(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        Comment comment = this.getCommentById(id);
        if (comment != null) {
            comment.setIsDeleted(true); // xóa mềm
            s.merge(comment);
        }
    }

    @Override
    public List<Comment> getCommentsByPostId(int postId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Comment> q = b.createQuery(Comment.class);
        Root<Comment> root = q.from(Comment.class);
        q.select(root);

        q.where(b.equal(root.get("postId").get("postId"), postId),
                b.equal(root.get("isDeleted"), false));

        Query query = s.createQuery(q);
        return query.getResultList();
    }
}

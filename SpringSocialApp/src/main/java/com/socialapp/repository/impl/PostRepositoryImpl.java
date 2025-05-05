/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.repository.impl;

import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import com.socialapp.repository.PostRepository;
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
public class PostRepositoryImpl implements PostRepository {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Post> getPosts(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Post> q = b.createQuery(Post.class);
        Root<Post> root = q.from(Post.class);
        q.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();

            // Lọc theo nội dung
            String content = params.get("content");
            if (content != null && !content.isEmpty()) {
                predicates.add(b.like(root.get("content"), String.format("%%%s%%", content)));
            }

            // Lọc theo userId
            String userId = params.get("userId");
            if (userId != null && !userId.isEmpty()) {
                predicates.add(b.equal(root.get("userId").get("userId"), Integer.parseInt(userId)));
            }

            // Lọc theo ngày (fromDate và toDate)
            String fromDate = params.get("fromDate");
            if (fromDate != null && !fromDate.isEmpty()) {
                predicates.add(b.greaterThanOrEqualTo(root.get("createdAt"), java.sql.Timestamp.valueOf(fromDate + " 00:00:00")));
            }

            String toDate = params.get("toDate");
            if (toDate != null && !toDate.isEmpty()) {
                predicates.add(b.lessThanOrEqualTo(root.get("createdAt"), java.sql.Timestamp.valueOf(toDate + " 23:59:59")));
            }

            // Lọc bài viết chưa xóa
            predicates.add(b.equal(root.get("isDeleted"), false));

            // Áp dụng các điều kiện lọc
            q.where(predicates.toArray(Predicate[]::new));

            // Sắp xếp theo ngày tạo
            String orderBy = params.get("orderBy");
            if (orderBy != null && !orderBy.isEmpty()) {
                q.orderBy(b.desc(root.get(orderBy)));
            } else {
                q.orderBy(b.desc(root.get("createdAt")));
            }
        }

        Query query = s.createQuery(q);

        // Phân trang
        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            query.setMaxResults(PAGE_SIZE);
            query.setFirstResult((page - 1) * PAGE_SIZE);
        }

        return query.getResultList();
    }

    @Override
    public Post getPostById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Post.class, id);
    }

    @Override
    public Post addOrUpdatePost(Post post) {
        Session s = this.factory.getObject().getCurrentSession();
        if (post.getPostId() == null) {
            s.persist(post);
        } else {
            s.merge(post);
        }
        return post;
    }

    @Override
    public void deletePost(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        Post post = this.getPostById(id);
        if (post != null) {
            post.setIsDeleted(true); // xóa mềm
            s.merge(post);
        }
    }

    @Override
    public List<Post> getPostsByUserId(int userId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Post> q = b.createQuery(Post.class);
        Root<Post> root = q.from(Post.class);
        q.select(root);

        q.where(
                b.equal(root.get("userId").get("userId"), userId),
                b.equal(root.get("isDeleted"), false)
        );

        Query query = s.createQuery(q);
        return query.getResultList();
    }

    @Override
    public List<Comment> getCommentsByPostId(int postId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Comment> q = b.createQuery(Comment.class);
        Root<Comment> root = q.from(Comment.class);
        q.select(root);

        q.where(
                b.equal(root.get("postId").get("postId"), postId)
        );

        Query query = s.createQuery(q);
        return query.getResultList();
    }
}

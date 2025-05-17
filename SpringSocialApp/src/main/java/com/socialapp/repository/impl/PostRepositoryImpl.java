package com.socialapp.repository.impl;

import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import com.socialapp.repository.PostRepository;
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
import org.hibernate.query.Query;

@Repository
@Transactional
public class PostRepositoryImpl implements PostRepository {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private LocalSessionFactoryBean factory;

    private Session getCurrentSession() {
        return this.factory.getObject().getCurrentSession();
    }

    @Override
    public List<Post> getPosts(Map<String, String> params) {
        Session s = getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Post> q = b.createQuery(Post.class);
        Root<Post> root = q.from(Post.class);
        q.select(root);

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            // Lọc theo nội dung
            String content = params.get("content");
            if (content != null && !content.isEmpty()) {
                predicates.add(b.like(root.get("content"), "%" + content + "%"));
            }

            // Lọc theo userId
            String userId = params.get("userId");
            if (userId != null && !userId.isEmpty()) {
                predicates.add(b.equal(root.get("user").get("userId"), Integer.parseInt(userId)));
                // Lưu ý: userId trong Post có thể là "user" (đối tượng User), bạn check entity của bạn
            }

            // Lọc theo ngày fromDate
            String fromDate = params.get("fromDate");
            if (fromDate != null && !fromDate.isEmpty()) {
                predicates.add(b.greaterThanOrEqualTo(root.get("createdAt"), java.sql.Timestamp.valueOf(fromDate + " 00:00:00")));
            }

            // Lọc theo ngày toDate
            String toDate = params.get("toDate");
            if (toDate != null && !toDate.isEmpty()) {
                predicates.add(b.lessThanOrEqualTo(root.get("createdAt"), java.sql.Timestamp.valueOf(toDate + " 23:59:59")));
            }
        }

        // Lọc bài viết chưa xóa (soft delete)
        predicates.add(b.equal(root.get("isDeleted"), false));

        // Áp dụng các điều kiện lọc
        q.where(predicates.toArray(new Predicate[0]));

        // Sắp xếp
        if (params != null && params.get("orderBy") != null && !params.get("orderBy").isEmpty()) {
            q.orderBy(b.desc(root.get(params.get("orderBy"))));
        } else {
            q.orderBy(b.desc(root.get("createdAt")));
        }

        Query<Post> query = s.createQuery(q);

        // Phân trang
        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            query.setFirstResult((page - 1) * PAGE_SIZE);
            query.setMaxResults(PAGE_SIZE);
        }

        return query.getResultList();
    }

    @Override
    public Post getPostById(int id) {
        return getCurrentSession().get(Post.class, id);
    }

    @Override
    public Post addOrUpdatePost(Post post) {
        Session s = getCurrentSession();
        if (post.getPostId() == null) {
            s.persist(post);
        } else {
            s.merge(post);
        }
        return post;
    }

    @Override
    public void deletePost(int id) {
        Session s = getCurrentSession();
        Post post = getPostById(id);
        if (post != null) {
            post.setIsDeleted(true); // xóa mềm
            s.merge(post);
        }
    }

    @Override
    public List<Post> getPostsByUserId(int userId) {
        Session s = getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Post> q = b.createQuery(Post.class);
        Root<Post> root = q.from(Post.class);
        q.select(root);

        q.where(
                b.equal(root.get("user").get("userId"), userId),
                b.equal(root.get("isDeleted"), false)
        );

        Query<Post> query = s.createQuery(q);
        return query.getResultList();
    }

    @Override
    public List<Comment> getCommentsByPostId(int postId) {
        Session s = getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Comment> q = b.createQuery(Comment.class);
        Root<Comment> root = q.from(Comment.class);
        q.select(root);

        q.where(
                b.equal(root.get("postId").get("postId"), postId)
        );

        Query<Comment> query = s.createQuery(q);
        return query.getResultList();
    }

    @Override
    public long countPosts() {
        Session session = getCurrentSession();
        Query<Long> query = session.createQuery("SELECT COUNT(p.postId) FROM Post p WHERE p.isDeleted = false", Long.class);
        Long count = query.getSingleResult();
        return count != null ? count : 0;
    }

    @Override
    public int countPostsCreatedToday() {
        Session session = getCurrentSession();
        Query<Long> query = session.createQuery(
                "SELECT COUNT(p.postId) FROM Post p WHERE p.isDeleted = false AND DATE(p.createdAt) = CURRENT_DATE", Long.class);
        Long count = query.getSingleResult();
        return count != null ? count.intValue() : 0;
    }

}

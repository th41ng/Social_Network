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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
@Transactional
public class PostRepositoryImpl implements PostRepository {

    private static final int PAGE_SIZE = 10; // Kích thước trang mặc định
    private static final Logger logger = LoggerFactory.getLogger(PostRepositoryImpl.class);

    @Autowired
    private LocalSessionFactoryBean factory;

    private Session getCurrentSession() {
        return this.factory.getObject().getCurrentSession();
    }

    // Phương thức private để xây dựng các predicates dựa trên params
    private List<Predicate> buildPredicates(Map<String, String> params, CriteriaBuilder b, Root<Post> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            // Lọc theo nội dung (param name: "content")
            String content = params.get("content");
            if (content != null && !content.isEmpty()) {
                predicates.add(b.like(root.get("content"), "%" + content + "%"));
            }

            // Lọc theo userId (param name: "userId")
            String userIdStr = params.get("userId");
            if (userIdStr != null && !userIdStr.isEmpty()) {
                try {
                    predicates.add(b.equal(root.get("userId").get("id"), Integer.parseInt(userIdStr)));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid userId format: {}", userIdStr);
                }
            }

            // Lọc theo ngày fromDate (param name: "fromDate")
            String fromDateStr = params.get("fromDate");
            if (fromDateStr != null && !fromDateStr.isEmpty()) {
                try {
                    predicates.add(b.greaterThanOrEqualTo(root.get("createdAt"), java.sql.Timestamp.valueOf(fromDateStr + " 00:00:00")));
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid fromDate format: {}", fromDateStr, e);
                }
            }

            // Lọc theo ngày toDate (param name: "toDate")
            String toDateStr = params.get("toDate");
            if (toDateStr != null && !toDateStr.isEmpty()) {
                try {
                    predicates.add(b.lessThanOrEqualTo(root.get("createdAt"), java.sql.Timestamp.valueOf(toDateStr + " 23:59:59")));
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid toDate format: {}", toDateStr, e);
                }
            }
        }

        // Luôn lọc các bài viết chưa bị xóa mềm
        predicates.add(b.equal(root.get("isDeleted"), false));

        return predicates;
    }

    @Override
    public List<Post> getPosts(Map<String, String> params) {
        Session s = getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Post> q = b.createQuery(Post.class);
        Root<Post> root = q.from(Post.class);
        q.select(root);

        List<Predicate> predicates = buildPredicates(params, b, root);
        q.where(predicates.toArray(new Predicate[0]));

        // Sắp xếp
        if (params != null && params.get("orderBy") != null && !params.get("orderBy").isEmpty()) {
            // Ví dụ: orderBy=createdAt, orderDir=asc/desc
            String orderByField = params.get("orderBy");
            String orderDir = params.getOrDefault("orderDir", "desc").toLowerCase();
            if ("asc".equals(orderDir)) {
                q.orderBy(b.asc(root.get(orderByField)));
            } else {
                q.orderBy(b.desc(root.get(orderByField)));
            }
        } else {
            q.orderBy(b.desc(root.get("createdAt"))); // Mặc định sắp xếp theo ngày tạo giảm dần
        }

        Query<Post> query = s.createQuery(q);

        // Phân trang
        if (params != null && params.containsKey("page")) {
            int page = 1;
            try {
                page = Integer.parseInt(params.get("page"));
                if (page < 1) page = 1; // Đảm bảo trang không nhỏ hơn 1
            } catch (NumberFormatException e) {
                logger.warn("Invalid page number in params: '{}'. Defaulting to page 1.", params.get("page"));
                // Giữ page = 1 nếu có lỗi
            }
            query.setFirstResult((page - 1) * PAGE_SIZE);
            query.setMaxResults(PAGE_SIZE);
        }

        return query.getResultList();
    }

    @Override
    public long countPosts(Map<String, String> params) { // Chấp nhận params
        Session session = getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = b.createQuery(Long.class);
        Root<Post> root = cq.from(Post.class);
        cq.select(b.count(root));

        List<Predicate> predicates = buildPredicates(params, b, root); // Tái sử dụng logic predicates
        cq.where(predicates.toArray(new Predicate[0]));

        Query<Long> query = session.createQuery(cq);
        Long count = query.getSingleResult();
        return count != null ? count : 0L;
    }

    @Override
    public Post getPostById(int id) {
        Session s = getCurrentSession();
        Post post = s.get(Post.class, id);
        // Trả về null nếu không tìm thấy hoặc đã bị xóa mềm
        return (post != null && !post.getIsDeleted()) ? post : null;
    }

    @Override
    public Post addOrUpdatePost(Post post) {
        Session s = getCurrentSession();
        if (post.getPostId() == null) {
            s.persist(post);
        } else {
            s.merge(post); // Hoặc s.update(post) tùy theo cấu hình cascade và trạng thái của entity
        }
        return post;
    }

    @Override
    public void deletePost(int id) {
        Session s = getCurrentSession();
        Post post = this.getPostById(id); // Sử dụng getPostById đã kiểm tra isDeleted
        if (post != null) {
            post.setIsDeleted(true); // Thực hiện xóa mềm
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
            b.equal(root.get("userId").get("id"), userId), // Giả định Post có trường userId là đối tượng User với trường id
            b.equal(root.get("isDeleted"), false)
        );
        q.orderBy(b.desc(root.get("createdAt")));

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
            b.equal(root.get("postId").get("postId"), postId) // Giả định Comment có trường postId là đối tượng Post với trường postId
            // Có thể thêm điều kiện  b.equal(root.get("isDeleted"), false) nếu comment cũng có xóa mềm
        );
        q.orderBy(b.asc(root.get("createdAt"))); // Sắp xếp comment theo thời gian tạo

        Query<Comment> query = s.createQuery(q);
        return query.getResultList();
    }

    @Override
    public int countPostsCreatedToday() {
        Session session = getCurrentSession();
        // Đảm bảo truy vấn này cũng chỉ đếm các bài viết chưa bị xóa
        Query<Long> query = session.createQuery(
            "SELECT COUNT(p.postId) FROM Post p WHERE p.isDeleted = false AND DATE(p.createdAt) = CURRENT_DATE", Long.class);
        Long count = query.getSingleResult();
        return count != null ? count.intValue() : 0;
    }
}

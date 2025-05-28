package com.socialapp.service.impl;

import com.socialapp.configs.UserRole;
import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import com.socialapp.pojo.User;
import com.socialapp.repository.PostRepository;
import com.socialapp.service.PostService;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;
    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    @Override
    public List<Post> getPosts(Map<String, String> params) {
        return this.postRepository.getPosts(params);
    }

    @Override
    public Post getPostById(int id) {
        return this.postRepository.getPostById(id);
    }

    @Override
    public Post addOrUpdatePost(Post post) {
        return this.postRepository.addOrUpdatePost(post);
    }

    @Override
    public List<Post> getPostsByUserId(int userId) {
        return this.postRepository.getPostsByUserId(userId);
    }

    @Override
    public List<Comment> getCommentsByPostId(int postId) {
        return this.postRepository.getCommentsByPostId(postId);
    }

    @Override
    public int countPostsCreatedToday() {
        return this.postRepository.countPostsCreatedToday();
    }

    // === PHƯƠNG THỨC DELETEPOST ĐÃ ĐƯỢC CẬP NHẬT VỚI KIỂM TRA QUYỀN ===
    @Override
    public void deletePost(int postId, User currentUser) {
        Post post = this.postRepository.getPostById(postId);
        if (post == null) {
            logger.warn("Người dùng (ID: {}) cố gắng xóa bài viết không tồn tại ID: {}",
                    (currentUser != null ? currentUser.getId() : "Không xác định"), postId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bài viết không tồn tại để xóa.");
        }

        if (currentUser == null) {
            logger.warn("Yêu cầu xóa bài viết ID: {} không có thông tin người dùng hợp lệ.", postId);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Hành động yêu cầu xác thực người dùng.");
        }

        boolean isAdmin = UserRole.ROLE_ADMIN.equals(currentUser.getRole());

        logger.debug("Người dùng '{}' (ID: {}) đang thực hiện xóa bài viết ID {}. Vai trò từ Pojo: {}. Là Admin: {}",
                currentUser.getUsername(), currentUser.getId(), postId, currentUser.getRole(), isAdmin);

        if (post.getUserId() != null && post.getUserId().getId().equals(currentUser.getId()) || isAdmin) {
            this.postRepository.deletePost(postId);
            logger.info("Bài viết ID {} đã được xóa thành công bởi người dùng ID {} (Admin: {})", postId, currentUser.getId(), isAdmin);
        } else {
            logger.warn("Người dùng ID {} (vai trò: {}, Admin: {}) bị từ chối quyền xóa bài viết ID {} (chủ sở hữu ID {})",
                    currentUser.getId(), currentUser.getRole(), isAdmin, postId, post.getUserId().getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xóa bài viết này.");
        }
    }
    
    // === CẬP NHẬT COUNTPOSTS ĐỂ HỖ TRỢ PHÂN TRANG ===
    @Override
    public long countPosts(Map<String, String> params) {
        return this.postRepository.countPosts(params);
    }
}

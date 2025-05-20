/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.service.impl;

import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import com.socialapp.pojo.User; // Thêm import User Pojo của bạn
import com.socialapp.repository.PostRepository;
import com.socialapp.service.PostService;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger; // Thêm import
import org.slf4j.LoggerFactory; // Thêm import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Thêm import
// Không cần import GrantedAuthority nếu kiểm tra trực tiếp qua trường 'role'
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException; // Thêm import

/**
 *
 * @author DELL G15
 */
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;
    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class); // Thêm logger

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
        // Nếu phương thức này cần logic liên quan đến người dùng hiện tại (ví dụ: gán userId khi tạo mới),
        // bạn có thể cần truyền đối tượng User vào đây.
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
    // Giả định interface PostService đã được cập nhật thành: void deletePost(int postId, User currentUser);
    @Override
    public void deletePost(int postId, User currentUser) { // Chữ ký phương thức đã thay đổi
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

        boolean isAdmin = false;
        if (currentUser.getRole() != null) { // Kiểm tra vai trò trực tiếp từ trường 'role' của User Pojo
            // So sánh không phân biệt hoa thường, kiểm tra cả "ADMIN" và "ROLE_ADMIN" cho linh hoạt
            isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole()) || 
                      "ROLE_ADMIN".equalsIgnoreCase(currentUser.getRole());
        }
        
        logger.debug("Người dùng '{}' (ID: {}) đang thực hiện xóa bài viết ID {}. Vai trò từ Pojo: {}. Là Admin (đã tính toán): {}", 
            currentUser.getUsername(), currentUser.getId(), postId, currentUser.getRole(), isAdmin);

        // Cho phép xóa nếu người dùng là chủ sở hữu bài viết HOẶC là ADMIN
        // Giả định:
        // 1. post.getUserId() trả về đối tượng User của người tạo bài viết.
        // 2. User Pojo của bạn có phương thức getId() trả về ID (ví dụ: Integer).
        if (post.getUserId() != null && post.getUserId().getId().equals(currentUser.getId()) || isAdmin) {
            this.postRepository.deletePost(postId); // Gọi phương thức repository để xóa
            logger.info("Bài viết ID {} đã được xóa thành công bởi người dùng ID {} (Admin: {})", postId, currentUser.getId(), isAdmin);
        } else {
            logger.warn("Người dùng ID {} (vai trò: {}, Admin: {}) bị từ chối quyền xóa bài viết ID {} (chủ sở hữu ID {})", 
                currentUser.getId(), currentUser.getRole(), isAdmin, postId, post.getUserId().getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xóa bài viết này.");
        }
    }
}
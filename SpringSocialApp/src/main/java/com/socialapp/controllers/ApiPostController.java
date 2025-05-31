package com.socialapp.controllers;

import com.socialapp.dto.CommentDTO;
import com.socialapp.dto.PostDTO;
import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import com.socialapp.pojo.User;
import com.socialapp.service.CommentService;
import com.socialapp.service.PostApiService;
import com.socialapp.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiPostController {

    private static final Logger logger = LoggerFactory.getLogger(ApiPostController.class);

    @Autowired
    private PostApiService postApiService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    private String getUsernameFromPrincipal(Object principal) {
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }
        return principal != null ? principal.toString() : null;
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostDTO>> getPosts(@RequestParam(required = false) Map<String, String> params) {
        List<PostDTO> posts = postApiService.getPosts(params);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPostById(@PathVariable("postId") int id) {
        PostDTO postDTO = postApiService.getPostById(id);
        if (postDTO != null) {
            return ResponseEntity.ok(postDTO);
        } else {
            logger.warn("Không tìm thấy Post với ID: {}", id);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Bài viết không tồn tại hoặc đã bị xóa."));
        }
    }

    @PostMapping(path = "/posts", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createOrUpdatePost(
            @ModelAttribute Post postFromRequest,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal().toString())) {
            logger.warn("Người dùng chưa xác thực cố gắng đăng hoặc cập nhật bài viết.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Yêu cầu đăng nhập để thực hiện hành động này."));
        }

        String username = getUsernameFromPrincipal(authentication.getPrincipal());
        if (username == null) {
            logger.error("Không thể lấy username từ principal cho hoạt động tạo/cập nhật bài viết.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Thông tin xác thực không hợp lệ."));
        }

        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            logger.error("Không tìm thấy thông tin người dùng đã xác thực trong DB: {}", username);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Lỗi thông tin người dùng hệ thống."));
        }

        boolean isCreatingNewPost = (postFromRequest.getPostId() == null);
        if (isCreatingNewPost
                && (postFromRequest.getContent() == null || postFromRequest.getContent().trim().isEmpty())
                && (postFromRequest.getImageFile() == null || postFromRequest.getImageFile().isEmpty())) {
            logger.warn("Nội dung bài viết và hình ảnh đều rỗng khi tạo bài viết mới bởi user: {}", username);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Nội dung hoặc hình ảnh không được để trống khi tạo bài viết."));
        }

        try {

            PostDTO resultPostDTO = postApiService.addOrUpdatePost(postFromRequest, currentUser);

            HttpStatus status = isCreatingNewPost ? HttpStatus.CREATED : HttpStatus.OK;
            String action = isCreatingNewPost ? "tạo mới" : "cập nhật";
            logger.info("Bài viết ID: {} đã được {} thành công bởi user: {}", resultPostDTO.getPostId(), action, username);
            return new ResponseEntity<>(resultPostDTO, status);

        } catch (EntityNotFoundException e) {
            logger.warn("Lỗi khi cập nhật bài viết bởi user {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            logger.warn("Lỗi bảo mật khi {} bài viết bởi user {}: {}", (isCreatingNewPost ? "tạo mới" : "cập nhật"), username, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.warn("Lỗi dữ liệu không hợp lệ khi {} bài viết bởi user {}: {}", (isCreatingNewPost ? "tạo mới" : "cập nhật"), username, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) { 
            String action = isCreatingNewPost ? "tạo mới" : "cập nhật";
            logger.error("Lỗi không xác định khi {} bài viết bởi user {}: {}", action, username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Lỗi máy chủ không xác định khi xử lý bài viết."));
        }
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable("postId") int postId, Authentication authentication) { // Sửa kiểu trả về
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal().toString())) {
            logger.warn("Unauthorized attempt to delete post {}", postId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Yêu cầu đăng nhập."));
        }

        String username = getUsernameFromPrincipal(authentication.getPrincipal());
        if (username == null) {
            logger.error("Không thể lấy username từ principal cho hoạt động xóa bài viết ID: {}", postId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Thông tin xác thực không hợp lệ."));
        }

        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            logger.error("User {} (principal) not found in DB for delete operation on post {}", username, postId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Thông tin người dùng hệ thống không hợp lệ."));
        }

        try {
            boolean deleted = postApiService.deletePost(postId, currentUser); 
            if (deleted) { 
                logger.info("Post {} deleted successfully by user {}", postId, username);
                return ResponseEntity.noContent().build(); 
            } else {
                
                logger.warn("Post deletion indicated as failed (returned false) for post {} by user {}.", postId, username);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Không thể xóa bài viết do lỗi không xác định từ service."));
            }
        } catch (EntityNotFoundException e) {
            logger.warn("Post {} not found for deletion by user {}: {}", postId, username, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            logger.warn("Security exception for user {} deleting post {}: {}", username, postId, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) { 
            logger.error("Error deleting post {} for user {}: {}", postId, username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Lỗi máy chủ không xác định khi xóa bài viết."));
        }
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> addCommentToPost(
            @PathVariable("postId") int postId,
            @RequestBody Map<String, String> payload,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal().toString())) {
            logger.warn("Người dùng chưa xác thực cố gắng thêm bình luận cho bài viết ID: {}", postId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Yêu cầu đăng nhập để bình luận."));
        }

        String content = payload.get("content");
        if (content == null || content.trim().isEmpty()) {
            logger.warn("Nội dung bình luận rỗng cho bài viết ID: {}", postId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Nội dung bình luận không được để trống."));
        }

        String username = getUsernameFromPrincipal(authentication.getPrincipal());
        if (username == null) {
            logger.error("Không thể lấy username từ principal cho hoạt động comment bài viết ID: {}", postId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Thông tin xác thực không hợp lệ."));
        }
        User currentUser = userService.getUserByUsername(username);

        if (currentUser == null) {
            logger.error("Không tìm thấy thông tin người dùng đã xác thực trong DB: {} khi comment bài viết ID: {}", username, postId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Lỗi thông tin người dùng hệ thống."));
        }

        try {
            Comment newCommentEntity = commentService.createComment(postId, currentUser.getId(), content.trim());

            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setCommentId(newCommentEntity.getCommentId());
            commentDTO.setContent(newCommentEntity.getContent());
            if (newCommentEntity.getUserId() != null) {
                commentDTO.setUserId(newCommentEntity.getUserId().getId());
                commentDTO.setUserFullName(newCommentEntity.getUserId().getFullName());
                commentDTO.setUserAvatar(newCommentEntity.getUserId().getAvatar());
            }
            if (newCommentEntity.getCreatedAt() != null) {
                commentDTO.setCreatedAt(newCommentEntity.getCreatedAt().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime());
            }
            commentDTO.setReactions(new HashMap<>());

            logger.info("Người dùng ID: {} (username: {}) đã thêm bình luận ID: {} cho bài viết ID: {}",
                    currentUser.getId(), username, newCommentEntity.getCommentId(), postId);
            return new ResponseEntity<>(commentDTO, HttpStatus.CREATED);

        } catch (EntityNotFoundException e) {
            logger.warn("Lỗi khi thêm bình luận cho bài viết ID {} (user: {}): {}", postId, username, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.warn("Lỗi dữ liệu không hợp lệ khi thêm bình luận cho bài viết ID {} (user: {}): {}", postId, username, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Lỗi không xác định khi thêm bình luận cho bài viết ID {} (user: {}): {}", postId, username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Lỗi máy chủ khi thêm bình luận."));
        }
    }

    @PostMapping("/posts/{postId}/toggle-comment-lock")
    public ResponseEntity<?> togglePostCommentLock(
            @PathVariable("postId") Integer postId,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal().toString())) {
            logger.warn("Người dùng chưa xác thực yêu cầu thay đổi trạng thái khóa bình luận cho bài viết ID: {}", postId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Yêu cầu đăng nhập."));
        }

        String username = getUsernameFromPrincipal(authentication.getPrincipal());
        if (username == null) {
            logger.error("Không thể lấy username từ principal cho hoạt động khóa/mở khóa comment của bài viết ID: {}", postId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Thông tin xác thực không hợp lệ."));
        }
        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            logger.error("Không tìm thấy người dùng {} trong DB khi cố gắng khóa/mở khóa comment của bài viết ID: {}", username, postId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Lỗi thông tin người dùng hệ thống."));
        }

        try {

            PostDTO updatedPost = postApiService.toggleCommentLock(postId, currentUser);
            return ResponseEntity.ok(updatedPost);
        } catch (EntityNotFoundException e) {
            logger.warn("Không tìm thấy bài viết ID {} để thay đổi trạng thái khóa bình luận, user {}: {}", postId, username, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            logger.warn("Lỗi bảo mật khi user {} thay đổi trạng thái khóa bình luận cho bài viết ID {}: {}", username, postId, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Lỗi không mong muốn khi user {} thay đổi trạng thái khóa bình luận cho bài viết ID {}: {}", username, postId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Lỗi máy chủ không xác định."));
        }
    }
}

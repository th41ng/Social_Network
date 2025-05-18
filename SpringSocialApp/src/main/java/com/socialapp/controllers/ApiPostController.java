package com.socialapp.controllers;

import com.socialapp.dto.CommentDTO;
import com.socialapp.dto.PostDTO;
import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import com.socialapp.pojo.User;
import com.socialapp.service.CommentService;
import com.socialapp.service.PostApiService;
import com.socialapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType; // QUAN TRỌNG: Đã import
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
// Không cần import MultipartFile ở đây nếu dùng @ModelAttribute và Pojo đã có

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin // Xem xét cấu hình CORS global nếu cần
public class ApiPostController {

    private static final Logger logger = LoggerFactory.getLogger(ApiPostController.class);

    @Autowired
    private PostApiService postApiService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @GetMapping("/posts")
    public ResponseEntity<List<PostDTO>> getPosts(@RequestParam(required = false) Map<String, String> params) {
        List<PostDTO> posts = postApiService.getPosts(params);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable("postId") int id) {
        PostDTO postDTO = postApiService.getPostById(id);
        if (postDTO != null) {
            return ResponseEntity.ok(postDTO);
        } else {
            logger.warn("Không tìm thấy Post với ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    // === PHƯƠNG THỨC ĐÃ ĐƯỢC CẬP NHẬT ĐẦY ĐỦ CHO VIỆC ĐĂNG BÀI (CẢ TEXT VÀ ẢNH) ===
    @PostMapping(path = "/posts", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createOrUpdatePost(
            @ModelAttribute Post post, // Sử dụng @ModelAttribute để bind form-data (bao gồm file)
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal().toString())) {
            logger.warn("Người dùng chưa xác thực cố gắng đăng hoặc cập nhật bài viết.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Yêu cầu đăng nhập để thực hiện hành động này.");
        }

        // Kiểm tra xem có nội dung hoặc ảnh không (cho bài viết mới)
        boolean isCreatingNewPost = (post.getPostId() == null); // Giả sử postId là null cho bài viết mới
        if (isCreatingNewPost &&
            (post.getContent() == null || post.getContent().trim().isEmpty()) && 
            (post.getImageFile() == null || post.getImageFile().isEmpty())) {
             logger.warn("Nội dung bài viết và hình ảnh đều rỗng khi tạo bài viết mới.");
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nội dung hoặc hình ảnh không được để trống khi tạo bài viết.");
        }

        try {
            Object principal = authentication.getPrincipal();
            String username;
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }
            
            User currentUser = userService.getUserByUsername(username);
            if (currentUser == null) {
                logger.error("Không tìm thấy thông tin người dùng đã xác thực: {}", username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Lỗi thông tin người dùng.");
            }

            // Gán người dùng hiện tại cho bài viết nếu là bài viết mới
            if (isCreatingNewPost) {
                post.setUserId(currentUser);
            }
            // Nếu là update, PostApiServiceImpl nên có logic kiểm tra quyền sở hữu bài viết

            PostDTO resultPostDTO = postApiService.addOrUpdatePost(post); 

            if (resultPostDTO != null) {
                HttpStatus status = isCreatingNewPost ? HttpStatus.CREATED : HttpStatus.OK;
                logger.info("Bài viết đã được {} thành công với ID: {}", (isCreatingNewPost ? "tạo mới" : "cập nhật"), resultPostDTO.getPostId());
                return new ResponseEntity<>(resultPostDTO, status);
            } else {
                logger.error("Không thể {} bài viết.", (isCreatingNewPost ? "tạo mới" : "cập nhật"));
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xử lý bài viết.");
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Lỗi dữ liệu khi {} bài viết: {}", (post.getPostId() == null ? "tạo mới" : "cập nhật"), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } 
        catch (Exception e) {
            logger.error("Lỗi không xác định khi {} bài viết: {}", (post.getPostId() == null ? "tạo mới" : "cập nhật"), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi máy chủ khi xử lý bài viết.");
        }
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable("postId") int id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal().toString())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // TODO: Thêm logic kiểm tra quyền xóa (user là chủ sở hữu hoặc admin)
        // Ví dụ: if (!postApiService.canUserDeletePost(authentication.getName(), id)) { return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); }
        
        postApiService.deletePost(id); 
        logger.info("Yêu cầu xóa bài viết với ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> addCommentToPost(
            @PathVariable("postId") int postId,
            @RequestBody Map<String, String> payload,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal().toString())) {
            logger.warn("Người dùng chưa xác thực cố gắng thêm bình luận cho bài viết ID: {}", postId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Yêu cầu đăng nhập để bình luận.");
        }

        String content = payload.get("content");
        if (content == null || content.trim().isEmpty()) {
            logger.warn("Nội dung bình luận rỗng cho bài viết ID: {}", postId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nội dung bình luận không được để trống.");
        }

        try {
            Object principal = authentication.getPrincipal();
            String username = (principal instanceof UserDetails) ? ((UserDetails)principal).getUsername() : principal.toString();
            User currentUser = userService.getUserByUsername(username);

            if (currentUser == null) {
                logger.error("Không tìm thấy thông tin người dùng đã xác thực: {}", username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Không tìm thấy thông tin người dùng.");
            }

            Comment newCommentEntity = commentService.createComment(postId, currentUser.getId(), content);
            
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setCommentId(newCommentEntity.getCommentId());
            commentDTO.setContent(newCommentEntity.getContent());
            if (newCommentEntity.getUserId() != null) {
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

        } catch (IllegalArgumentException e) {
            logger.warn("Lỗi dữ liệu khi thêm bình luận (postId: {}): {}", postId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Lỗi không xác định khi thêm bình luận cho bài viết ID: {}", postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi máy chủ khi thêm bình luận.");
        }
    }
}
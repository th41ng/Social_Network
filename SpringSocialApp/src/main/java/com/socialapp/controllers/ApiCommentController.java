
package com.socialapp.controllers;

import com.socialapp.dto.CommentDTO;
import com.socialapp.pojo.User;
import com.socialapp.service.CommentApiService; 
import com.socialapp.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiCommentController {

    private static final Logger logger = LoggerFactory.getLogger(ApiCommentController.class);

    @Autowired
    private CommentApiService commentApiService; 

    @Autowired
    private UserService userService;

    // Helper method để lấy username từ Principal
    private String getUsernameFromPrincipal(Object principal) {
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }
        return principal != null ? principal.toString() : null;
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable("commentId") Integer commentId,
            @RequestBody Map<String, String> payload,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal().toString())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Yêu cầu đăng nhập."));
        }
        String content = payload.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Nội dung bình luận không được rỗng."));
        }

        String username = getUsernameFromPrincipal(authentication.getPrincipal());
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Thông tin xác thực không hợp lệ."));
        }
        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
           
            logger.error("Critical: User object not found in database for authenticated username: {}", username);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Lỗi thông tin người dùng hệ thống."));
        }

        try {
            
            CommentDTO updatedComment = commentApiService.updateCommentApi(commentId, content.trim(), currentUser);
            logger.info("Comment ID {} updated successfully by user {}", commentId, username);
            return ResponseEntity.ok(updatedComment);
        } catch (EntityNotFoundException e) {
            logger.warn("Comment not found for update (ID: {}): {}", commentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            logger.warn("Security violation attempting to update comment ID {} by user {}: {}", commentId, username, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating comment ID {}: {}", commentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Lỗi máy chủ khi cập nhật bình luận."));
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable("commentId") Integer commentId,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal().toString())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Yêu cầu đăng nhập."));
        }

        String username = getUsernameFromPrincipal(authentication.getPrincipal());
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Thông tin xác thực không hợp lệ."));
        }
        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            logger.error("Critical: User object not found in database for authenticated username: {}", username);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Lỗi thông tin người dùng hệ thống."));
        }

        try {
           
            boolean deleted = commentApiService.deleteCommentApi(commentId, currentUser);
          
            if (deleted) {
                logger.info("Comment ID {} marked as deleted successfully by user {}", commentId, username);
                return ResponseEntity.noContent().build(); 
            } else {
               
                logger.warn("Comment ID {} was not deleted by user {}, but no exception was thrown.", commentId, username);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Không thể xóa bình luận."));
            }

        } catch (EntityNotFoundException e) {
            logger.warn("Comment not found for deletion (ID: {}): {}", commentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            logger.warn("Security violation attempting to delete comment ID {} by user {}: {}", commentId, username, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting comment ID {}: {}", commentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Lỗi máy chủ khi xóa bình luận."));
        }
    }

  
}
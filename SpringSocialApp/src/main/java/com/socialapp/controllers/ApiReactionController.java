package com.socialapp.controllers;

import com.socialapp.pojo.User;
import com.socialapp.service.ReactionService;
import com.socialapp.service.UserService; // Interface UserService của bạn
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails; // Import UserDetails
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin // Bạn đã có @CrossOrigin ở các controller khác, có thể cấu hình global sau
public class ApiReactionController {

    private static final Logger logger = LoggerFactory.getLogger(ApiReactionController.class);

    @Autowired
    private ReactionService reactionService; // Service bạn vừa hoàn thiện

    @Autowired
    private UserService userService; // Service để lấy User Pojo từ username

    // DTO đơn giản để nhận "type" của reaction từ request body của frontend
    // Frontend sẽ gửi JSON: { "type": "like" }
    public static class ReactionRequest {
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    @PostMapping("/posts/{postId}/reactions")
    public ResponseEntity<Map<String, Long>> reactToPost(
            @PathVariable("postId") Integer postId,
            @RequestBody ReactionRequest reactionRequest, // Nhận type từ body
            Authentication authentication) { // Spring Security sẽ cung cấp đối tượng này nếu user đã đăng nhập

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Attempt to react to post {} without authentication.", postId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Lấy thông tin user đang đăng nhập
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.getUserByUsername(userDetails.getUsername()); // Dùng hàm bạn đã có trong UserService

        if (currentUser == null) {
            // Trường hợp này hiếm khi xảy ra nếu token hợp lệ và user có trong DB
            logger.error("Authenticated user {} (from token) not found in database.", userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Hoặc một lỗi cụ thể hơn
        }

        // Kiểm tra request body
        if (reactionRequest == null || reactionRequest.getType() == null || reactionRequest.getType().isEmpty()) {
            logger.warn("Reaction request for post {} is missing 'type'.", postId);
            return ResponseEntity.badRequest().body(null); // Trả về lỗi nếu không có type
        }

        try {
            logger.info("User {} reacting with type '{}' to post {}", currentUser.getId(), reactionRequest.getType(), postId);
            Map<String, Long> updatedReactions = reactionService.handlePostReaction(
                    postId,
                    currentUser.getId(), // Truyền ID của User Pojo
                    reactionRequest.getType());
            return ResponseEntity.ok(updatedReactions); // Trả về số lượng reaction mới
        } catch (IllegalArgumentException e) {
            logger.warn("Bad request for post {} reaction: {}", postId, e.getMessage());
            return ResponseEntity.badRequest().body(null); // Hoặc trả về Map.of("error", e.getMessage())
        } catch (Exception e) {
            logger.error("Error processing reaction for post {}: {}", postId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/comments/{commentId}/reactions")
    public ResponseEntity<Map<String, Long>> reactToComment(
            @PathVariable("commentId") Integer commentId,
            @RequestBody ReactionRequest reactionRequest,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Attempt to react to comment {} without authentication.", commentId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.getUserByUsername(userDetails.getUsername());

        if (currentUser == null) {
            logger.error("Authenticated user {} (from token) not found in database.", userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        
        if (reactionRequest == null || reactionRequest.getType() == null || reactionRequest.getType().isEmpty()) {
            logger.warn("Reaction request for comment {} is missing 'type'.", commentId);
            return ResponseEntity.badRequest().body(null);
        }

        try {
            logger.info("User {} reacting with type '{}' to comment {}", currentUser.getId(), reactionRequest.getType(), commentId);
            Map<String, Long> updatedReactions = reactionService.handleCommentReaction(
                    commentId,
                    currentUser.getId(),
                    reactionRequest.getType());
            return ResponseEntity.ok(updatedReactions);
        } catch (IllegalArgumentException e) {
            logger.warn("Bad request for comment {} reaction: {}", commentId, e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (UnsupportedOperationException e) { // Bắt lỗi nếu CommentRepository chưa sẵn sàng
             logger.warn("Comment reaction feature not supported or not configured: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null); // Hoặc một lỗi phù hợp
        } catch (Exception e) {
            logger.error("Error processing reaction for comment {}: {}", commentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
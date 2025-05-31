package com.socialapp.controllers;

import com.socialapp.pojo.User;
import com.socialapp.service.ReactionService;
import com.socialapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException; 
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiReactionController {

    private static final Logger logger = LoggerFactory.getLogger(ApiReactionController.class);

    @Autowired
    private ReactionService reactionService;

    @Autowired
    private UserService userService;

    public static class ReactionRequest {
        private String type;
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    @PostMapping("/posts/{postId}/reactions")
    public ResponseEntity<?> reactToPost(
            @PathVariable("postId") Integer postId,
            @RequestBody ReactionRequest reactionRequest,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Attempt to react to post {} without authentication.", postId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication required."));
        }

        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        User currentUser = userService.getUserByUsername(username);

        if (currentUser == null) {
            logger.error("Authenticated user {} (from principal) not found in database.", username);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "User details not found for authenticated user. Please contact support."));
        }

        if (reactionRequest == null || reactionRequest.getType() == null || reactionRequest.getType().trim().isEmpty()) {
            logger.warn("Reaction request for post {} is missing or has empty 'type'.", postId);
            return ResponseEntity.badRequest().body(Map.of("error", "Reaction type cannot be empty."));
        }

        try {
            logger.info("User ID {} (username: {}) reacting with type '{}' to post {}", currentUser.getId(), username, reactionRequest.getType(), postId);
            Map<String, Long> updatedReactions = reactionService.handlePostReaction(
                    postId,
                    currentUser.getId(),
                    reactionRequest.getType().trim());
            return ResponseEntity.ok(updatedReactions);
        } catch (IllegalArgumentException e) {
            logger.warn("Bad request for post {} reaction: {}", postId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) { 
            logger.warn("Entity not found for post reaction (postId: {}): {}", postId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Post not found."));
        } catch (Exception e) {
            logger.error("Error processing reaction for post {}: {}", postId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "An unexpected error occurred while processing your reaction."));
        }
    }

    @PostMapping("/comments/{commentId}/reactions")
    public ResponseEntity<?> reactToComment(
            @PathVariable("commentId") Integer commentId,
            @RequestBody ReactionRequest reactionRequest,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Attempt to react to comment {} without authentication.", commentId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication required."));
        }

        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        User currentUser = userService.getUserByUsername(username);

        if (currentUser == null) {
            logger.error("Authenticated user {} (from principal) not found in database.", username);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "User details not found for authenticated user. Please contact support."));
        }

        if (reactionRequest == null || reactionRequest.getType() == null || reactionRequest.getType().trim().isEmpty()) {
            logger.warn("Reaction request for comment {} is missing or has empty 'type'.", commentId);
            return ResponseEntity.badRequest().body(Map.of("error", "Reaction type cannot be empty."));
        }

        try {
            logger.info("User ID {} (username: {}) reacting with type '{}' to comment {}", currentUser.getId(), username, reactionRequest.getType(), commentId);
            Map<String, Long> updatedReactions = reactionService.handleCommentReaction(
                    commentId,
                    currentUser.getId(),
                    reactionRequest.getType().trim());
            return ResponseEntity.ok(updatedReactions);
        } catch (IllegalArgumentException e) {
            logger.warn("Bad request for comment {} reaction: {}", commentId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) { 
            logger.warn("Entity not found for comment reaction (commentId: {}): {}", commentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Comment not found."));
        } catch (UnsupportedOperationException e) {
            logger.warn("Comment reaction feature not supported or not configured: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error processing reaction for comment {}: {}", commentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "An unexpected error occurred while processing your reaction."));
        }
    }
}
package com.socialapp.service.impl;

import com.socialapp.dto.CommentDTO;
import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import com.socialapp.pojo.User;
import com.socialapp.repository.CommentRepository;
import com.socialapp.repository.PostRepository;
import com.socialapp.service.CommentApiService;
import com.socialapp.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import java.time.ZoneId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("commentApiServiceImpl")
public class CommentApiServiceImpl implements CommentApiService {

    private static final Logger logger = LoggerFactory.getLogger(CommentApiServiceImpl.class);

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentService commentService;

   
    private CommentDTO convertToDTO(Comment comment) {
        if (comment == null) {
            return null;
        }
        CommentDTO dto = new CommentDTO();
        dto.setCommentId(comment.getCommentId());
        dto.setContent(comment.getContent());

        if (comment.getUserId() != null) {
            dto.setUserId(comment.getUserId().getId());
            dto.setUserFullName(comment.getUserId().getFullName());
            dto.setUserAvatar(comment.getUserId().getAvatar());
        }
        if (comment.getCreatedAt() != null) {
            dto.setCreatedAt(comment.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        if (comment.getUpdatedAt() != null) {
            dto.setUpdatedAt(comment.getUpdatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        return dto;
    }

    @Override
    @Transactional
    public CommentDTO createCommentApi(int postId, String content, User currentUser) {
        logger.info("API request from user {} (ID: {}) to create comment on post ID: {}",
                currentUser.getUsername(), currentUser.getId(), postId);

        Post post = this.postRepository.getPostById(postId);

        if (post == null || (post.getIsDeleted() != null && post.getIsDeleted())) {
            logger.warn("API createComment: Post not found or deleted with ID: {} for user {}", postId, currentUser.getUsername());
            throw new EntityNotFoundException("Post not found or deleted with ID: " + postId);
        }
        if (post.getIsCommentLocked() != null && post.getIsCommentLocked()) {
            logger.warn("API createComment: User {} attempted to comment on locked post ID {}", currentUser.getUsername(), postId);
            throw new SecurityException("This post is locked for comments.");
        }

        Comment newComment = new Comment();
        newComment.setContent(content.trim());
        newComment.setPostId(post);
        newComment.setUserId(currentUser);

        Comment savedComment = this.commentService.addOrUpdateComment(newComment);
        logger.info("Comment created with ID {} on post ID {} by user {}", savedComment.getCommentId(), postId, currentUser.getUsername());
        return convertToDTO(savedComment);
    }

    @Override
    @Transactional
    public CommentDTO updateCommentApi(Integer commentId, String content, User currentUser) {
        logger.info("API request from user {} (ID: {}) to update comment ID: {}",
                currentUser.getUsername(), currentUser.getId(), commentId);

        Comment existingComment = commentRepository.getCommentById(commentId);

        if (existingComment == null || (existingComment.getIsDeleted() != null && existingComment.getIsDeleted())) {
            logger.warn("API updateComment: Comment ID {} not found or deleted for user {}", commentId, currentUser.getUsername());
            throw new EntityNotFoundException("Comment not found with id: " + commentId + " or it has been deleted.");
        }

        // Kiểm tra quyền sở hữu
        if (!existingComment.getUserId().getId().equals(currentUser.getId())) {
            logger.warn("User {} (ID: {}) attempted to update comment {} owned by user ID: {}. Permission denied.",
                    currentUser.getUsername(), currentUser.getId(), commentId, existingComment.getUserId().getId());
            throw new SecurityException("User not authorized to update this comment.");
        }

        existingComment.setContent(content.trim());
        Comment updatedComment = this.commentService.addOrUpdateComment(existingComment);
        logger.info("Comment ID {} updated by user {}", commentId, currentUser.getUsername());
        return convertToDTO(updatedComment);
    }

    @Override
    @Transactional
    public boolean deleteCommentApi(Integer commentId, User currentUser) {
        logger.info("API request from user {} (ID: {}) to delete comment ID: {}",
                currentUser.getUsername(), currentUser.getId(), commentId);

        Comment commentToDelete = commentRepository.getCommentById(commentId);

        if (commentToDelete == null) {
            logger.warn("API deleteComment: Comment ID {} not found for user {}", commentId, currentUser.getUsername());
            throw new EntityNotFoundException("Comment not found with id: " + commentId + " for deletion.");
        }

        if (commentToDelete.getIsDeleted() != null && commentToDelete.getIsDeleted()) {
            logger.info("API deleteComment: Comment ID {} was already soft-deleted. User {}.", commentId, currentUser.getUsername());
            return true; 
        }

        // Kiểm tra quyền: người viết comment hoặc chủ bài viết
        User commentAuthor = commentToDelete.getUserId();
        Post associatedPost = commentToDelete.getPostId();
        User postOwner = (associatedPost != null) ? associatedPost.getUserId() : null;

        boolean canDelete = false;
        if (commentAuthor != null && commentAuthor.getId().equals(currentUser.getId())) {
            canDelete = true; // Người dùng là tác giả bình luận
        } else if (postOwner != null && postOwner.getId().equals(currentUser.getId())) {
            canDelete = true; // Người dùng là chủ bài viết
        }

        if (!canDelete) {
            String authorIdLog = (commentAuthor != null && commentAuthor.getId() != null) ? commentAuthor.getId().toString() : "N/A";
            String ownerIdLog = (postOwner != null && postOwner.getId() != null) ? postOwner.getId().toString() : "N/A";
            logger.warn("User {} (ID: {}) unauthorized to delete comment ID {} (Author ID: {}, Post Owner ID: {}).",
                    currentUser.getUsername(), currentUser.getId(), commentId, authorIdLog, ownerIdLog);
            throw new SecurityException("User not authorized to delete this comment.");
        }

        this.commentRepository.deleteComment(commentId);

        logger.info("Comment ID {} soft-deleted by user {}", commentId, currentUser.getUsername());
        return true;
    }
}

package com.socialapp.service.impl;

import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import com.socialapp.pojo.User;
import com.socialapp.repository.CommentRepository;
import com.socialapp.repository.PostRepository;
import com.socialapp.repository.UserRepository;
import com.socialapp.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("commentServiceImpl")
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Comment> getComments(Map<String, String> params) {
        logger.debug("Fetching comments with params: {}", params);
        return this.commentRepository.getComments(params);
    }

    @Override
    public Comment getCommentById(int id) {
        logger.debug("Fetching comment by ID: {}", id);
        return this.commentRepository.getCommentById(id);
    }

    @Override
    @Transactional
    public Comment addOrUpdateComment(Comment comment) {
        if (comment.getCommentId() == null) {
            logger.info("Adding new comment for post ID: {} by user ID: {}",
                    comment.getPostId() != null ? comment.getPostId().getPostId() : "N/A",
                    comment.getUserId() != null ? comment.getUserId().getId() : "N/A");
            if (comment.getCreatedAt() == null) {
                comment.setCreatedAt(new Date());
            }
            if (comment.getIsDeleted() == null) {
                comment.setIsDeleted(false);
            }
        } else {
            logger.info("Updating comment ID: {}", comment.getCommentId());
        }
        comment.setUpdatedAt(new Date());
        return this.commentRepository.addOrUpdateComment(comment);
    }

    @Override
    @Transactional
    public void deleteComment(int id) {

        logger.warn("Attempting to delete comment with ID: {} using backend deleteComment(id).", id);
        Comment comment = this.commentRepository.getCommentById(id);
        if (comment != null) {
            if (comment.getIsDeleted() == null || !comment.getIsDeleted()) {

                this.commentRepository.deleteComment(id);
                logger.info("Comment ID: {} has been processed for deletion via backend service.", id);
            } else {
                logger.info("Comment ID: {} was already marked as deleted. No action taken.", id);
            }
        } else {
            logger.warn("Comment ID: {} not found for deletion.", id);
            throw new EntityNotFoundException("Comment not found with ID: " + id + " for deletion.");
        }
    }

    @Override
    public List<Comment> getCommentsByPostId(int postId) {
        logger.debug("Fetching comments for post ID: {}", postId);
        // Kiểm tra xem post có tồn tại và không bị xóa không trước khi lấy comment
        Post post = postRepository.getPostById(postId);
        if (post == null || (post.getIsDeleted() != null && post.getIsDeleted())) {
            logger.warn("Attempting to get comments for non-existent or deleted post ID: {}", postId);

            return List.of();
        }
        return this.commentRepository.getCommentsByPostId(postId);
    }

    @Override
    @Transactional
    public Comment createComment(int postId, int userId, String content) {
        logger.info("Backend request to create comment on post ID: {} by user ID: {}", postId, userId);
        User user = this.userRepository.getUserById(userId);
        Post post = this.postRepository.getPostById(postId);

        if (user == null) {
            logger.warn("Backend createComment: User not found with ID: {}", userId);
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
        if (post == null || (post.getIsDeleted() != null && post.getIsDeleted())) {
            logger.warn("Backend createComment: Post not found or deleted with ID: {}", postId);
            throw new EntityNotFoundException("Post not found or deleted with ID: " + postId);
        }
        if (post.getIsCommentLocked() != null && post.getIsCommentLocked()) {
            logger.warn("Backend createComment: User ID {} attempted to comment on locked post ID {}", userId, postId);
            throw new SecurityException("This post is locked for comments.");
        }

        Comment newComment = new Comment();
        newComment.setContent(content.trim());
        newComment.setPostId(post);
        newComment.setUserId(user);

        return this.addOrUpdateComment(newComment);
    }
}

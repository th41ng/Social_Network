
package com.socialapp.service.impl;

import com.socialapp.dto.CommentDTO; 
import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import com.socialapp.pojo.User;
import com.socialapp.repository.CommentRepository;
import com.socialapp.repository.PostRepository;
import com.socialapp.repository.UserRepository;
import com.socialapp.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import java.time.ZoneId; 
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

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
            // Chuyển đổi Date sang LocalDateTime
            dto.setCreatedAt(comment.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }

      
        if (comment.getUpdatedAt() != null) {
            // Chuyển đổi Date sang LocalDateTime (nếu Comment entity dùng Date và CommentDTO dùng LocalDateTime)
            dto.setUpdatedAt(comment.getUpdatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
      
        return dto;
    }

    @Override
    public List<Comment> getComments(Map<String, String> params) {
        return this.commentRepository.getComments(params);
    }

    @Override
    public Comment getCommentById(int id) {
        return this.commentRepository.getCommentById(id);
    }

    @Override
    @Transactional
    public Comment addOrUpdateComment(Comment comment) {
        if (comment.getCommentId() == null) { // Chỉ set khi tạo mới
            if (comment.getCreatedAt() == null) {
                comment.setCreatedAt(new Date());
            }
            if (comment.getIsDeleted() == null) { // Đảm bảo isDeleted được khởi tạo
                comment.setIsDeleted(false);
            }
        }
        comment.setUpdatedAt(new Date()); // Luôn cập nhật hoặc set thời gian sửa đổi
        return this.commentRepository.addOrUpdateComment(comment);
    }

    @Override
    @Transactional
    @Deprecated // Nên dùng deleteComment(Integer, User) có kiểm tra quyền
    public void deleteComment(int id) {
        
        this.commentRepository.deleteComment(id);
        logger.warn("Called deprecated deleteComment(id) for comment ID: {}. Consider using deleteComment(id, currentUser).", id);
    }

    @Override
    public List<Comment> getCommentsByPostId(int postId) {
        
        return this.commentRepository.getCommentsByPostId(postId);
    }

    @Override
    @Transactional
    public Comment createComment(int postId, int userId, String content) {
        User user = this.userRepository.getUserById(userId); 
        Post post = this.postRepository.getPostById(postId);

        if (user == null) {
            logger.warn("Attempting to create comment: User not found with ID: {}", userId);
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
        if (post == null || (post.getIsDeleted() != null && post.getIsDeleted())) {
            logger.warn("Attempting to create comment: Post not found or deleted with ID: {}", postId);
            throw new EntityNotFoundException("Post not found or deleted with ID: " + postId);
        }
        if (post.getIsCommentLocked() != null && post.getIsCommentLocked()) {
            logger.warn("User ID {} attempted to comment on locked post ID {}", userId, postId);
            throw new SecurityException("This post is locked for comments.");
        }

        Comment newComment = new Comment();
        newComment.setContent(content.trim());
        newComment.setPostId(post);
        newComment.setUserId(user);
       
        return this.addOrUpdateComment(newComment);
    }

    @Override
    @Transactional
    public CommentDTO updateComment(Integer commentId, String content, User currentUser) {
        Comment existingComment = commentRepository.getCommentById(commentId);

        if (existingComment == null || (existingComment.getIsDeleted() != null && existingComment.getIsDeleted())) {
            logger.warn("Attempt to update non-existent or deleted comment ID {} by user {}", commentId, currentUser.getUsername());
            throw new EntityNotFoundException("Comment not found with id: " + commentId + " or it has been deleted.");
        }

        // Kiểm tra quyền sở hữu
        if (!existingComment.getUserId().getId().equals(currentUser.getId())) {

            logger.warn("User {} (ID: {}) attempted to update comment {} owned by user ID: {}. Permission denied.",
                    currentUser.getUsername(), currentUser.getId(), commentId, existingComment.getUserId().getId());
            throw new SecurityException("User not authorized to update this comment.");
            // }
        }

        existingComment.setContent(content.trim());
        // addOrUpdateComment sẽ tự động cập nhật `updatedAt`
        Comment updatedComment = this.addOrUpdateComment(existingComment);
        logger.info("User {} (ID: {}) updated comment ID {}", currentUser.getUsername(), currentUser.getId(), commentId);
        return convertToDTO(updatedComment); // Trả về DTO
    }

    @Override
    @Transactional
    public boolean deleteComment(Integer commentId, User currentUser) {
        Comment commentToDelete = commentRepository.getCommentById(commentId);

        if (commentToDelete == null) {
            logger.warn("Attempt to delete non-existent comment ID {} by user {}", commentId, currentUser.getUsername());
            throw new EntityNotFoundException("Comment not found with id: " + commentId + " for deletion.");
        }

        // Nếu comment đã bị xóa mềm rồi, không cần làm gì thêm, coi như thành công
        if (commentToDelete.getIsDeleted() != null && commentToDelete.getIsDeleted()) {
            logger.info("Comment ID {} was already soft-deleted. No further action taken for user {}.", commentId, currentUser.getUsername());
            return true;
        }

        // Kiểm tra quyền sở hữu
        if (!commentToDelete.getUserId().getId().equals(currentUser.getId())) {

            logger.warn("User {} (ID: {}) attempted to delete comment {} owned by user ID: {}. Permission denied.",
                    currentUser.getUsername(), currentUser.getId(), commentId, commentToDelete.getUserId().getId());
            throw new SecurityException("User not authorized to delete this comment.");
            // }
        }

        commentRepository.deleteComment(commentId); // Gọi phương thức xóa mềm của repository
        // Đảm bảo phương thức này cũng cập nhật `updatedAt`
        logger.info("User {} (ID: {}) soft-deleted comment ID {}", currentUser.getUsername(), currentUser.getId(), commentId);
        return true;
    }
}

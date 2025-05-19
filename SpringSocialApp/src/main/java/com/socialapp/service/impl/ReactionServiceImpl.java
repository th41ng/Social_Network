package com.socialapp.service.impl;

import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import com.socialapp.pojo.Reaction;
import com.socialapp.pojo.User;
import com.socialapp.repository.CommentRepository;
import com.socialapp.repository.PostRepository;
import com.socialapp.repository.ReactionRepository;
import com.socialapp.repository.UserRepository;
import com.socialapp.service.ReactionService;
import org.slf4j.Logger; // SỬ DỤNG SLF4J LOGGER
import org.slf4j.LoggerFactory; // SỬ DỤNG SLF4J LOGGER
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ReactionServiceImpl implements ReactionService {

    // === THÊM LOGGER ===
    private static final Logger logger = LoggerFactory.getLogger(ReactionServiceImpl.class);

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository; // Vẫn giữ lại, có thể cần cho các logic khác

    @Autowired // BỎ (required = false) ĐỂ SPRING BÁO LỖI NGAY NẾU KHÔNG TÌM THẤY BEAN
    private CommentRepository commentRepository;

    @Override
    @Transactional
    public Map<String, Long> handlePostReaction(Integer postId, Integer userId, String reactionType) {
        logger.info("Xử lý reaction cho postId: {}, userId: {}, loại: {}", postId, userId, reactionType);
        User user = userRepository.getUserById(userId);
        Post post = postRepository.getPostById(postId);

        if (user == null) {
            logger.warn("Không tìm thấy User ID: {} khi reaction cho Post ID: {}", userId, postId);
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        if (post == null) {
            logger.warn("Không tìm thấy Post ID: {} khi User ID: {} reaction", postId, userId);
            throw new IllegalArgumentException("Post not found with ID: " + postId);
        }

        Optional<Reaction> existingReactionOpt = reactionRepository.findByUserAndPost(user, post);

        if (existingReactionOpt.isPresent()) {
            Reaction existingReaction = existingReactionOpt.get();
            logger.debug("Tìm thấy reaction đã tồn tại cho Post {}: {}", postId, existingReaction);
            if (existingReaction.getReactionType().equals(reactionType)) {
                logger.info("User {} đang xóa reaction '{}' khỏi Post {}", userId, reactionType, postId);
                reactionRepository.deleteReaction(existingReaction.getReactionId());
            } else {
                logger.info("User {} đang thay đổi reaction trên Post {} từ '{}' thành '{}'", userId, postId, existingReaction.getReactionType(), reactionType);
                existingReaction.setReactionType(reactionType);
                existingReaction.setCreatedAt(new Date());
                reactionRepository.addOrUpdateReaction(existingReaction);
            }
        } else {
            logger.info("User {} đang thêm reaction mới '{}' vào Post {}", userId, reactionType, postId);
            Reaction newReaction = new Reaction();
            newReaction.setUserId(user);
            newReaction.setPostId(post);
            newReaction.setCommentId(null);
            newReaction.setReactionType(reactionType);
            newReaction.setCreatedAt(new Date());
            reactionRepository.addOrUpdateReaction(newReaction);
        }
        Map<String, Long> updatedReactions = reactionRepository.countReactionsByPostId(postId);
        logger.info("Số lượng reactions đã cập nhật cho Post {}: {}", postId, updatedReactions);
        return updatedReactions;
    }

    @Override
    @Transactional
    public Map<String, Long> handleCommentReaction(Integer commentId, Integer userId, String reactionType) {
        logger.info("Xử lý reaction cho commentId: {}, userId: {}, loại: {}", commentId, userId, reactionType);

        if (commentRepository == null) { // Kiểm tra này vẫn hữu ích dù đã bỏ required=false
            logger.error("CommentRepository chưa được inject. Không thể xử lý reaction cho comment.");
            throw new UnsupportedOperationException("Chức năng reaction cho comment yêu cầu CommentRepository phải được cấu hình.");
        }

        User user = userRepository.getUserById(userId);
        if (user == null) {
            logger.warn("Không tìm thấy User ID: {} khi reaction cho Comment ID: {}", userId, commentId);
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        Comment comment = commentRepository.getCommentById(commentId);
        if (comment == null) {
            logger.warn("Không tìm thấy Comment ID: {} khi User ID: {} reaction", commentId, userId);
            throw new IllegalArgumentException("Comment not found with ID: " + commentId);
        }

        // === KIỂM TRA QUAN TRỌNG: Comment có được liên kết với Post không? ===
        Post postOfComment = comment.getPostId(); // Lấy đối tượng Post từ Comment
        if (postOfComment == null) {
            logger.error("NGHIÊM TRỌNG: Comment ID {} không được liên kết với bất kỳ Post nào (comment.getPostId() trả về null). Không thể xử lý reaction.", commentId);
            // Lỗi này chỉ ra vấn đề toàn vẹn dữ liệu hoặc lỗi trong logic tạo/lấy comment.
            // Ném ra một lỗi cụ thể hơn để dễ dàng xác định từ log.
            throw new IllegalStateException("Comment ID " + commentId + " không có thông tin bài viết hợp lệ (post_id is null). Không thể xử lý reaction.");
        }
        logger.debug("Comment ID {} thuộc về Post ID {}", commentId, postOfComment.getPostId());
        // === KẾT THÚC KIỂM TRA ===

        Optional<Reaction> existingReactionOpt = reactionRepository.findByUserAndComment(user, comment);

        if (existingReactionOpt.isPresent()) {
            Reaction existingReaction = existingReactionOpt.get();
            logger.debug("Tìm thấy reaction đã tồn tại cho Comment {}: {}", commentId, existingReaction);
            if (existingReaction.getReactionType().equals(reactionType)) {
                logger.info("User {} đang xóa reaction '{}' khỏi Comment {}", userId, reactionType, commentId);
                reactionRepository.deleteReaction(existingReaction.getReactionId());
            } else {
                logger.info("User {} đang thay đổi reaction trên Comment {} từ '{}' thành '{}'", userId, commentId, existingReaction.getReactionType(), reactionType);
                existingReaction.setReactionType(reactionType);
                existingReaction.setCreatedAt(new Date());
                reactionRepository.addOrUpdateReaction(existingReaction);
            }
        } else {
            logger.info("User {} đang thêm reaction mới '{}' vào Comment {}", userId, reactionType, commentId);
            Reaction newReaction = new Reaction();
            newReaction.setUserId(user);
            newReaction.setPostId(postOfComment); // Sử dụng postOfComment đã được kiểm tra
            newReaction.setCommentId(comment);
            newReaction.setReactionType(reactionType);
            newReaction.setCreatedAt(new Date());
            reactionRepository.addOrUpdateReaction(newReaction);
        }
        
        Map<String, Long> updatedReactions = reactionRepository.countReactionsByCommentId(commentId);
        logger.info("Số lượng reactions đã cập nhật cho Comment {}: {}", commentId, updatedReactions);
        return updatedReactions;
    }

    // --- CÁC PHƯƠNG THỨC KHÁC GIỮ NGUYÊN ---
    @Override
    public Reaction addOrUpdateReaction(Reaction reaction) {
        return this.reactionRepository.addOrUpdateReaction(reaction);
    }

    @Override
    public void deleteReaction(int id) {
        this.reactionRepository.deleteReaction(id);
    }

    @Override
    public List<Reaction> getReactionsByPostId(int postId) {
        return this.reactionRepository.getReactionsByPostId(postId);
    }

    @Override
    public List<Reaction> getReactionsByCommentId(int commentId) {
        return this.reactionRepository.getReactionsByCommentId(commentId);
    }

    @Override
    public Map<String, Long> countReactionsByPostId(int postId) {
        return this.reactionRepository.countReactionsByPostId(postId);
    }

    @Override
    public Map<String, Long> countReactionsByCommentId(int commentId) {
        return this.reactionRepository.countReactionsByCommentId(commentId);
    }
}
// File: com/socialapp/service/impl/CommentServiceImpl.java
package com.socialapp.service.impl;

import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post; // Import Post Pojo
import com.socialapp.pojo.User; // Import User Pojo
import com.socialapp.repository.CommentRepository;
import com.socialapp.repository.PostRepository;   // === THÊM INJECT ===
import com.socialapp.repository.UserRepository;   // === THÊM INJECT ===
import com.socialapp.service.CommentService;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger; // Thêm logger
import org.slf4j.LoggerFactory; // Thêm logger
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Quan trọng

@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository; // === INJECT PostRepository ===

    @Autowired
    private UserRepository userRepository; // === INJECT UserRepository ===

    @Override
    public List<Comment> getComments(Map<String, String> params) {
        return this.commentRepository.getComments(params);
    }

    @Override
    public Comment getCommentById(int id) {
        return this.commentRepository.getCommentById(id);
    }

    @Override
    @Transactional // Đảm bảo phương thức này cũng có @Transactional
    public Comment addOrUpdateComment(Comment comment) {
        // Có thể thêm logic kiểm tra comment.getCreatedAt() == null thì set new Date() ở đây
        // nếu muốn addOrUpdateComment cũng tự set thời gian cho comment mới.
        if (comment.getCreatedAt() == null) {
            comment.setCreatedAt(new Date());
        }
        if (comment.getIsDeleted() == null) {
            comment.setIsDeleted(false);
        }
        return this.commentRepository.addOrUpdateComment(comment);
    }

    @Override
    @Transactional // Đảm bảo phương thức này cũng có @Transactional
    public void deleteComment(int id) {
        // Cân nhắc việc xóa mềm ở đây nếu Comment Pojo có trường isDeleted
        // Ví dụ:
        // Comment comment = this.commentRepository.getCommentById(id);
        // if (comment != null) {
        //     comment.setIsDeleted(true);
        //     this.commentRepository.addOrUpdateComment(comment);
        // } else {
        //     logger.warn("Không tìm thấy comment với ID: {} để xóa mềm.", id);
        // }
        // Hoặc nếu bạn muốn xóa cứng:
        this.commentRepository.deleteComment(id);
    }

    @Override
    public List<Comment> getCommentsByPostId(int postId) {
        return this.commentRepository.getCommentsByPostId(postId);
    }

    // === TRIỂN KHAI PHƯƠNG THỨC createComment ===
    @Override
    @Transactional // Rất quan trọng cho các thao tác ghi vào database
    public Comment createComment(int postId, int userId, String content) {
        User user = this.userRepository.getUserById(userId);
        Post post = this.postRepository.getPostById(postId);

        if (user == null) {
            logger.warn("Cố gắng tạo comment nhưng không tìm thấy User với ID: {}", userId);
            throw new IllegalArgumentException("Không tìm thấy người dùng để bình luận (ID: " + userId + ")");
        }
        if (post == null) {
            logger.warn("Cố gắng tạo comment nhưng không tìm thấy Post với ID: {}", postId);
            throw new IllegalArgumentException("Không tìm thấy bài viết để bình luận (ID: " + postId + ")");
        }
        // Giả sử Post Pojo có trường isLocked để kiểm tra bài viết có bị khóa không
       if (post.getIsCommentLocked() != null && post.getIsCommentLocked()) { 
            logger.warn("User ID {} cố gắng bình luận vào bài viết ID {} đã bị khóa bình luận.", userId, postId);
            throw new IllegalArgumentException("Bài viết này đã bị khóa bình luận, không thể thêm bình luận mới.");
        }

        Comment newComment = new Comment();
        newComment.setContent(content.trim());
        newComment.setPostId(post);      // Gán đối tượng Post
        newComment.setUserId(user);      // Gán đối tượng User
        // Các giá trị mặc định sẽ được set trong addOrUpdateComment hoặc ở đây
        // newComment.setCreatedAt(new Date()); // Sẽ được set trong addOrUpdateComment nếu logic trên được giữ
        // newComment.setIsDeleted(false);   // Sẽ được set trong addOrUpdateComment nếu logic trên được giữ

        // Gọi phương thức addOrUpdateComment hiện có để lưu
        return this.addOrUpdateComment(newComment); 
    }
    // === KẾT THÚC TRIỂN KHAI createComment ===
}
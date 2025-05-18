package com.socialapp.service.impl;

import com.cloudinary.Cloudinary; // THÊM: Import Cloudinary
import com.cloudinary.utils.ObjectUtils; // THÊM: Import ObjectUtils
import com.socialapp.dto.CommentDTO;
import com.socialapp.dto.PostDTO;
import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import com.socialapp.pojo.User;
import com.socialapp.repository.PostRepository;
import com.socialapp.repository.ReactionRepository;
import com.socialapp.service.PostApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile; // THÊM: Import MultipartFile

import java.io.IOException; // THÊM: Import IOException
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date; // THÊM: Import Date cho createdAt/updatedAt của Post
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostApiServiceImpl implements PostApiService {

    private static final Logger logger = LoggerFactory.getLogger(PostApiServiceImpl.class);

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired // === THÊM INJECTION CHO CLOUDINARY ===
    private Cloudinary cloudinary; // Bạn cần có Bean Cloudinary được cấu hình trong Spring context

    // Phương thức convertToFullPostDTO giữ nguyên như bạn đã cung cấp ở lượt 38
    // (đã có logic set commentCount, comment.createdAt, comment.reactions)
    private PostDTO convertToFullPostDTO(Post post) {
        if (post == null) {
            logger.warn("Attempted to convert a null Post object to PostDTO.");
            return null;
        }

        PostDTO postDTO = new PostDTO();
        postDTO.setPostId(post.getPostId());
        postDTO.setContent(post.getContent());
        postDTO.setImage(post.getImage());

        if (post.getUserId() != null) {
            postDTO.setUserFullName(post.getUserId().getFullName());
            postDTO.setUserAvatar(post.getUserId().getAvatar());
        } else {
            logger.warn("Post with ID {} has a null userId.", post.getPostId());
            postDTO.setUserFullName("Người dùng không xác định");
            postDTO.setUserAvatar(null);
        }

        if (post.getCreatedAt() != null) {
            LocalDateTime createdAtLDT = post.getCreatedAt().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            postDTO.setCreatedAt(createdAtLDT);
        }

        if (post.getPostId() != null) {
            Map<String, Long> postReactions = reactionRepository.countReactionsByPostId(post.getPostId());
            postDTO.setReactions(postReactions);
        } else {
            // logger.warn("Post object (content: '{}') is missing postId, cannot fetch post reactions.", post.getContent()); // Bỏ log này nếu post mới chưa có ID
            postDTO.setReactions(Collections.emptyMap());
        }

        List<CommentDTO> commentDTOs = new ArrayList<>();
        if (post.getCommentSet() != null && !post.getCommentSet().isEmpty()) {
            commentDTOs = post.getCommentSet().stream()
                .filter(commentEntity -> {
                    if (commentEntity == null) {
                        logger.warn("Null comment found in CommentSet for Post ID: {}", post.getPostId());
                        return false;
                    }
                    return commentEntity.getIsDeleted() == null || !commentEntity.getIsDeleted();
                })
                .map(commentEntity -> {
                    CommentDTO commentDTO = new CommentDTO();
                    commentDTO.setCommentId(commentEntity.getCommentId());
                    commentDTO.setContent(commentEntity.getContent());

                    if (commentEntity.getUserId() != null) {
                        commentDTO.setUserFullName(commentEntity.getUserId().getFullName());
                        commentDTO.setUserAvatar(commentEntity.getUserId().getAvatar());
                    } else {
                        commentDTO.setUserFullName("Người dùng không xác định");
                        commentDTO.setUserAvatar(null);
                    }

                    if (commentEntity.getCreatedAt() != null) {
                        LocalDateTime commentCreatedAtLDT = commentEntity.getCreatedAt().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();
                        commentDTO.setCreatedAt(commentCreatedAtLDT);
                    } else {
                        logger.warn("Comment with ID {} (for Post ID: {}) has null createdAt.", commentEntity.getCommentId(), post.getPostId());
                    }

                    if (commentEntity.getCommentId() != null) {
                        Map<String, Long> commentReactions = reactionRepository.countReactionsByCommentId(commentEntity.getCommentId());
                        commentDTO.setReactions(commentReactions);
                    } else {
                        // logger.warn("Comment object is missing commentId for Post ID: {}, cannot fetch reactions for this comment.", post.getPostId());
                        commentDTO.setReactions(Collections.emptyMap());
                    }
                    return commentDTO;
                })
                .collect(Collectors.toList());
        }
        
        postDTO.setComments(commentDTOs);
        postDTO.setCommentCount(commentDTOs.size()); 

        return postDTO;
    }

    @Override
    @Transactional // Rất quan trọng cho các thao tác ghi/cập nhật
    public PostDTO addOrUpdatePost(Post post) { // Tham số đầu vào là Post Pojo
        // UserID đã được gán từ Controller cho bài viết mới
        // Đối với update, cần kiểm tra quyền sở hữu ở đây hoặc trong Controller/Security rules

        // Xử lý upload ảnh nếu có file được gửi lên từ @ModelAttribute Post post
        MultipartFile imageFile = post.getImageFile(); // Lấy file từ Pojo (Post Pojo cần có trường imageFile và getter/setter)
        
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Upload ảnh lên Cloudinary
                Map uploadResult = this.cloudinary.uploader().upload(imageFile.getBytes(),
                        ObjectUtils.asMap(
                            "resource_type", "auto",
                            "folder", "social_app_posts" // (Tùy chọn) Tạo thư mục trên Cloudinary
                        ));
                String imageUrl = (String) uploadResult.get("secure_url");
                post.setImage(imageUrl); // Cập nhật trường image của Post entity với URL từ Cloudinary
                logger.info("Ảnh đã được upload lên Cloudinary: {}", imageUrl);
            } catch (IOException e) {
                logger.error("Lỗi khi upload ảnh bài viết lên Cloudinary: {}", e.getMessage(), e);
                // Tùy theo yêu cầu:
                // 1. Ném Exception để báo lỗi cho người dùng và không tạo bài viết
                // throw new RuntimeException("Lỗi upload hình ảnh, không thể tạo bài viết.", e);
                // 2. Hoặc vẫn tạo bài viết nhưng không có ảnh (image sẽ là null hoặc ảnh cũ nếu là update)
                post.setImage(null); // Nếu muốn tạo bài viết không ảnh khi upload lỗi
            }
        } else {
            // Nếu không có file ảnh mới được gửi lên:
            // - Nếu là bài viết mới (post.getPostId() == null), trường image sẽ là null (hoặc giá trị mặc định nếu có).
            // - Nếu là cập nhật bài viết (post.getPostId() != null) và người dùng không gửi file mới,
            //   chúng ta cần đảm bảo không ghi đè ảnh cũ bằng null.
            //   Logic này thường được xử lý bằng cách chỉ cập nhật trường 'image' nếu imageFile mới được cung cấp.
            //   Nếu 'post' được fetch từ DB trước khi bind các giá trị mới, ảnh cũ sẽ được giữ lại.
            //   Hiện tại, nếu không có imageFile, trường image sẽ không bị thay đổi bởi đoạn code trên.
             if (post.getPostId() != null && post.getImage() == null && imageFile == null) {
                // Đây là trường hợp update và người dùng có thể muốn xóa ảnh cũ mà không upload ảnh mới.
                // Cần có cơ chế riêng để xử lý việc xóa ảnh (ví dụ: một checkbox "Xóa ảnh hiện tại").
                // Hiện tại, nếu imageFile không có, ảnh cũ sẽ không thay đổi (nếu được load đúng cách)
                // hoặc là null nếu post là đối tượng mới hoàn toàn được bind từ request mà không có ảnh.
            }
        }

        // Gán các giá trị mặc định/cập nhật cho bài viết
        if (post.getPostId() == null) { // Đây là bài viết mới
            post.setCreatedAt(new Date());   // Thời gian tạo
            post.setIsDeleted(false);      // Mặc định chưa xóa
            post.setIsCommentLocked(false); // Mặc định không khóa comment
        }
        post.setUpdatedAt(new Date());     // Luôn cập nhật thời gian sửa đổi lần cuối

        // Lưu vào DB thông qua Repository
        Post savedPost = this.postRepository.addOrUpdatePost(post); 

        if (savedPost != null && savedPost.getPostId() != null) {
            // Lấy lại Post entity từ DB để đảm bảo dữ liệu đầy đủ nhất,
            // đặc biệt là các trường được quản lý bởi JPA/Hibernate (như userId nếu là proxy)
            // và các collection (như commentSet) được khởi tạo đúng cách cho việc convert sang DTO.
            Post freshPost = this.postRepository.getPostById(savedPost.getPostId());
            if (freshPost != null) {
                return convertToFullPostDTO(freshPost);
            } else {
                logger.error("Không thể lấy lại thông tin bài viết sau khi lưu, ID: {}", savedPost.getPostId());
                // Fallback: cố gắng convert savedPost, nhưng có thể thiếu thông tin
                return convertToFullPostDTO(savedPost); 
            }
        } else {
            logger.error("Không thể lưu hoặc lấy ID cho bài viết sau khi xử lý. Nội dung: '{}'", post.getContent());
            return null; // Hoặc throw một exception cụ thể
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDTO> getPosts(Map<String, String> params) {
        List<Post> posts = this.postRepository.getPosts(params);
        if (posts == null || posts.isEmpty()) {
            return Collections.emptyList();
        }
        return posts.stream()
                    .map(this::convertToFullPostDTO)
                    .filter(postDTO -> postDTO != null) // Lọc ra những DTO không bị null
                    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PostDTO getPostById(int id) {
        Post post = this.postRepository.getPostById(id);
        return convertToFullPostDTO(post);
    }

    @Override
    @Transactional
    public void deletePost(int id) {
        // Cân nhắc kiểm tra xem bài viết có tồn tại không trước khi gọi delete
        // Hoặc để PostRepository xử lý (ví dụ: không làm gì nếu không tìm thấy)
        this.postRepository.deletePost(id);
        logger.info("Đã gửi yêu cầu xóa bài viết ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPostId(int postId) {
        // Phương thức này có thể không còn được sử dụng thường xuyên
        // nếu PostDTO của bạn đã trả về đầy đủ danh sách comments.
        return this.postRepository.getCommentsByPostId(postId);
    }
}
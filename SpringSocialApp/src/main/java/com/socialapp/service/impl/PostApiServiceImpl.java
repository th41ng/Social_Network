package com.socialapp.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.socialapp.dto.CommentDTO;
import com.socialapp.dto.PostDTO;
import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import com.socialapp.pojo.User;
import com.socialapp.repository.PostRepository;
import com.socialapp.repository.ReactionRepository;
import com.socialapp.service.PostApiService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

    @Autowired
    private Cloudinary cloudinary;

    private PostDTO convertToFullPostDTO(Post post) {
        if (post == null) {
            logger.warn("Attempted to convert a null Post object to PostDTO.");
            return null;
        }

        PostDTO postDTO = new PostDTO();
        postDTO.setPostId(post.getPostId());
        postDTO.setContent(post.getContent());
        postDTO.setImage(post.getImage());
        postDTO.setCommentLocked(post.getIsCommentLocked());

        if (post.getUserId() != null) {
            postDTO.setUserId(post.getUserId().getId());
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

        if (post.getUpdatedAt() != null) {
            LocalDateTime updatedAtLDT = post.getUpdatedAt().toInstant()
                    .atZone(ZoneId.systemDefault()) // nhất quán với createdAt
                    .toLocalDateTime();
            postDTO.setUpdatedAt(updatedAtLDT);
        }

        if (post.getPostId() != null) {
            Map<String, Long> postReactions = reactionRepository.countReactionsByPostId(post.getPostId());
            postDTO.setReactions(postReactions);
        } else {
            postDTO.setReactions(Collections.emptyMap());
        }

        List<CommentDTO> commentDTOs = new ArrayList<>();
        if (post.getCommentSet() != null && !post.getCommentSet().isEmpty()) {
            commentDTOs = post.getCommentSet().stream()
                    .filter(commentEntity -> commentEntity != null && (commentEntity.getIsDeleted() == null || !commentEntity.getIsDeleted()))
                    .map(commentEntity -> {
                        CommentDTO commentDTO = new CommentDTO();
                        commentDTO.setCommentId(commentEntity.getCommentId());
                        commentDTO.setContent(commentEntity.getContent());
                        if (commentEntity.getUserId() != null) {
                            commentDTO.setUserId(commentEntity.getUserId().getId());
                            commentDTO.setUserFullName(commentEntity.getUserId().getFullName());
                            commentDTO.setUserAvatar(commentEntity.getUserId().getAvatar());
                        } else {
                            commentDTO.setUserFullName("Người dùng không xác định");
                        }
                        if (commentEntity.getCreatedAt() != null) {
                            commentDTO.setCreatedAt(commentEntity.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                        }

                        if (commentEntity.getUpdatedAt() != null) { // Giả sử Comment entity có getUpdatedAt()
                            commentDTO.setUpdatedAt(commentEntity.getUpdatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                        }

                        if (commentEntity.getCommentId() != null) {
                            commentDTO.setReactions(reactionRepository.countReactionsByCommentId(commentEntity.getCommentId()));
                        } else {
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
    @Transactional
    public PostDTO addOrUpdatePost(Post postFromRequest, User currentUser) {
        MultipartFile imageFile = postFromRequest.getImageFile();

        if (postFromRequest.getPostId() != null) {
            Post existingPost = this.postRepository.getPostById(postFromRequest.getPostId());
            if (existingPost == null) {
                logger.warn("Post with ID {} not found for update by user {}", postFromRequest.getPostId(), currentUser.getUsername());
                throw new EntityNotFoundException("Post with ID " + postFromRequest.getPostId() + " not found.");
            }

            if (!existingPost.getUserId().getId().equals(currentUser.getId())) {

                logger.warn("User {} (ID: {}) attempted to update post {} owned by user ID: {}. Permission denied.",
                        currentUser.getUsername(), currentUser.getId(), existingPost.getPostId(), existingPost.getUserId().getId());
                throw new SecurityException("User not authorized to update this post.");
            }

            existingPost.setContent(postFromRequest.getContent());
            existingPost.setUpdatedAt(new Date());

            if (postFromRequest.isRemoveCurrentImage()) {
                if (existingPost.getImage() != null) {

                    logger.info("Removing image for post ID {} as requested by user {}.", existingPost.getPostId(), currentUser.getUsername());
                    existingPost.setImage(null); // Xóa URL ảnh trong DB

                }
            } else if (imageFile != null && !imageFile.isEmpty()) {
                try {

                    Map uploadResult = this.cloudinary.uploader().upload(imageFile.getBytes(),
                            ObjectUtils.asMap("resource_type", "auto", "folder", "social_app_posts"));
                    existingPost.setImage((String) uploadResult.get("secure_url"));

                    logger.info("Ảnh đã được CẬP NHẬT trên Cloudinary cho post ID {}: {}", existingPost.getPostId(), existingPost.getImage());
                } catch (IOException e) {
                    logger.error("Lỗi khi upload ảnh cập nhật cho bài viết ID {}: {}", existingPost.getPostId(), e.getMessage(), e);

                }
            }

            postFromRequest = existingPost;

        } else {
            postFromRequest.setUserId(currentUser);
            postFromRequest.setCreatedAt(new Date());
            postFromRequest.setUpdatedAt(new Date());
            postFromRequest.setIsDeleted(false);
            postFromRequest.setIsCommentLocked(false);

            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    Map uploadResult = this.cloudinary.uploader().upload(imageFile.getBytes(),
                            ObjectUtils.asMap("resource_type", "auto", "folder", "social_app_posts"));
                    postFromRequest.setImage((String) uploadResult.get("secure_url"));

                    logger.info("Ảnh đã được UPLOAD lên Cloudinary cho bài viết mới: {}", postFromRequest.getImage());
                } catch (IOException e) {
                    logger.error("Lỗi khi upload ảnh cho bài viết mới bởi user {}: {}", currentUser.getUsername(), e.getMessage(), e);
                    postFromRequest.setImage(null);
                }
            }
        }

        Post savedPost = this.postRepository.addOrUpdatePost(postFromRequest);
        Post freshPost = this.postRepository.getPostById(savedPost.getPostId());

        if (freshPost != null) {
            return convertToFullPostDTO(freshPost);
        } else {
            logger.error("CRITICAL: Không thể lấy lại thông tin bài viết ID: {} sau khi lưu.", savedPost.getPostId());

            return convertToFullPostDTO(savedPost);
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
                .filter(postDTO -> postDTO != null)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PostDTO getPostById(int id) {
        Post post = this.postRepository.getPostById(id);
        if (post == null || post.getIsDeleted()) {
            logger.warn("Attempted to get post by ID {} but it was not found or is deleted.", id);
            return null;
        }
        return convertToFullPostDTO(post);
    }

    @Override
    @Transactional
    public boolean deletePost(int postId, User currentUser) {
        Post post = this.postRepository.getPostById(postId);
        if (post == null) {
            logger.warn("Post with ID {} not found for deletion attempt by user {}.", postId, currentUser.getUsername());
            throw new EntityNotFoundException("Post not found with id " + postId + ".");
        }

        if (!post.getUserId().getId().equals(currentUser.getId())) {
            logger.warn("User {} (ID: {}) attempted to delete post {} owned by user ID: {}. Permission denied.",
                    currentUser.getUsername(), currentUser.getId(), postId, post.getUserId().getId());
            throw new SecurityException("User not authorized to delete this post.");
        }

        this.postRepository.deletePost(postId);
        logger.info("Post ID: {} soft-deleted by user ID: {}", postId, currentUser.getId());
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPostId(int postId) {

        Post post = this.postRepository.getPostById(postId);
        if (post == null || post.getIsDeleted()) {
            return Collections.emptyList();
        }
        return this.postRepository.getCommentsByPostId(postId).stream()
                .filter(c -> c.getIsDeleted() == null || !c.getIsDeleted())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PostDTO toggleCommentLock(Integer postId, User currentUser) {
        Post post = postRepository.getPostById(postId);
        if (post == null || (post.getIsDeleted() != null && post.getIsDeleted())) {
            logger.warn("Post not found or deleted (ID: {}) for toggling comment lock by user {}", postId, currentUser.getUsername());
            throw new EntityNotFoundException("Post not found with id: " + postId);
        }

        if (!post.getUserId().getId().equals(currentUser.getId())) {

            logger.warn("User {} attempted to toggle lock on post {} owned by user {}. Permission denied.",
                    currentUser.getUsername(), postId, post.getUserId().getUsername());
            throw new SecurityException("User not authorized to change comment lock status for this post.");

        }

        post.setIsCommentLocked(post.getIsCommentLocked() == null ? true : !post.getIsCommentLocked());
        post.setUpdatedAt(new Date());
        Post updatedPost = postRepository.addOrUpdatePost(post);
        logger.info("User {} {} comment lock for post ID {}",
                currentUser.getUsername(),
                (updatedPost.getIsCommentLocked() != null && updatedPost.getIsCommentLocked() ? "ENABLED" : "DISABLED"),
                postId);
        return convertToFullPostDTO(updatedPost);
    }

    @Transactional(readOnly = true)

    @Override
    public List<Post> getPostsByUserId(int userId) {
        try {

            List<Post> posts = postRepository.getPostsByUserId(userId);

            return posts.stream()
                    .filter(post -> post.getIsDeleted() == null || !post.getIsDeleted())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error while fetching posts for user ID {}: {}", userId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}

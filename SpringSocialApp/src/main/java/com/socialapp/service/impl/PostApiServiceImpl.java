package com.socialapp.service.impl;

import com.socialapp.dto.CommentDTO;
import com.socialapp.dto.PostDTO;
import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import com.socialapp.repository.PostRepository;
import com.socialapp.repository.ReactionRepository;
import com.socialapp.service.PostApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PostApiServiceImpl implements PostApiService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ReactionRepository reactionRepository;

    @Override
    @Transactional
    public List<PostDTO> getPosts(Map<String, String> params) {
        List<Post> posts = this.postRepository.getPosts(params);
        List<PostDTO> postDTOs = new ArrayList<>();

        for (Post post : posts) {
            String fullName = post.getUserId() != null ? post.getUserId().getFullName() : null;
            String avatar = post.getUserId() != null ? post.getUserId().getAvatar() : null;

            // Gán thông tin về hình ảnh vào PostDTO
            String imageUrl = post.getImage(); // Lấy đường dẫn hình ảnh từ cơ sở dữ liệu

            PostDTO postDTO = new PostDTO();
            postDTO.setPostId(post.getPostId());
            postDTO.setContent(post.getContent());
            postDTO.setUserFullName(fullName);
            postDTO.setUserAvatar(avatar);
            postDTO.setImage(imageUrl);  // Gán image vào PostDTO

            // Convert Date to LocalDateTime
            if (post.getCreatedAt() != null) {
                LocalDateTime createdAt = post.getCreatedAt().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                postDTO.setCreatedAt(createdAt);
            }

            // Get reactions for the post
            Map<String, Long> reactions = reactionRepository.countReactionsByPostId(post.getPostId());
            postDTO.setReactions(reactions);

            // Fetch and set comments for the post
            List<CommentDTO> commentDTOs = new ArrayList<>();
            if (post.getCommentSet() != null) {
                for (Comment c : post.getCommentSet()) {
                    if (Boolean.TRUE.equals(c.getIsDeleted())) continue;

                    String commenterName = c.getUserId() != null ? c.getUserId().getFullName() : null;
                    String commenterAvatar = c.getUserId() != null ? c.getUserId().getAvatar() : null;

                    // Create CommentDTO
                    CommentDTO commentDTO = new CommentDTO(
                            c.getCommentId(),
                            c.getContent(),
                            commenterName,
                            commenterAvatar
                    );

                    // Get reactions for each comment
                    Map<String, Long> commentReactions = reactionRepository.countReactionsByCommentId(c.getCommentId());
                    commentDTO.setReactions(commentReactions); // Set reactions for each comment

                    commentDTOs.add(commentDTO);
                }
            }

            postDTO.setComments(commentDTOs);
            postDTOs.add(postDTO);
        }

        return postDTOs;
    }

    @Override
    public PostDTO getPostById(int id) {
        Post post = this.postRepository.getPostById(id);
        if (post != null) {
            String fullName = post.getUserId() != null ? post.getUserId().getFullName() : null;
            String avatar = post.getUserId() != null ? post.getUserId().getAvatar() : null;

            // Gán thông tin về hình ảnh vào PostDTO
            String imageUrl = post.getImage(); // Lấy đường dẫn hình ảnh từ cơ sở dữ liệu

            PostDTO postDTO = new PostDTO();
            postDTO.setPostId(post.getPostId());
            postDTO.setContent(post.getContent());
            postDTO.setUserFullName(fullName);
            postDTO.setUserAvatar(avatar);
            postDTO.setImage(imageUrl);  // Gán image vào PostDTO

            if (post.getCreatedAt() != null) {
                LocalDateTime createdAt = post.getCreatedAt().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                postDTO.setCreatedAt(createdAt);
            }

            // Get reactions for the post
            Map<String, Long> reactions = reactionRepository.countReactionsByPostId(post.getPostId());
            postDTO.setReactions(reactions);

            List<CommentDTO> commentDTOs = new ArrayList<>();
            if (post.getCommentSet() != null) {
                for (Comment c : post.getCommentSet()) {
                    if (Boolean.TRUE.equals(c.getIsDeleted())) continue;

                    String commenterName = c.getUserId() != null ? c.getUserId().getFullName() : null;
                    String commenterAvatar = c.getUserId() != null ? c.getUserId().getAvatar() : null;

                    commentDTOs.add(new CommentDTO(
                            c.getCommentId(),
                            c.getContent(),
                            commenterName,
                            commenterAvatar
                    ));

                    // Get reactions for each comment
                    Map<String, Long> commentReactions = reactionRepository.countReactionsByCommentId(c.getCommentId());
                    commentDTOs.get(commentDTOs.size() - 1).setReactions(commentReactions); // Set reactions for the last added comment
                }
            }

            postDTO.setComments(commentDTOs);
            return postDTO;
        }
        return null;
    }

    @Override
    public PostDTO addOrUpdatePost(Post post) {
        Post savedPost = this.postRepository.addOrUpdatePost(post);
        String fullName = savedPost.getUserId() != null ? savedPost.getUserId().getFullName() : null;
        String avatar = savedPost.getUserId() != null ? savedPost.getUserId().getAvatar() : null;

        // Gán thông tin về hình ảnh vào PostDTO
        String imageUrl = savedPost.getImage(); // Lấy đường dẫn hình ảnh từ cơ sở dữ liệu

        PostDTO postDTO = new PostDTO();
        postDTO.setPostId(savedPost.getPostId());
        postDTO.setContent(savedPost.getContent());
        postDTO.setUserFullName(fullName);
        postDTO.setUserAvatar(avatar);
        postDTO.setImage(imageUrl);  // Gán image vào PostDTO

        if (savedPost.getCreatedAt() != null) {
            LocalDateTime createdAt = savedPost.getCreatedAt().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            postDTO.setCreatedAt(createdAt);
        }

        return postDTO;
    }

    @Override
    public void deletePost(int id) {
        this.postRepository.deletePost(id);
    }

    @Override
    public List<Comment> getCommentsByPostId(int postId) {
        return this.postRepository.getCommentsByPostId(postId);
    }
}

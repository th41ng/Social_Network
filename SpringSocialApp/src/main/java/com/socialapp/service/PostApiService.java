package com.socialapp.service;

import com.socialapp.dto.PostDTO;
import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import com.socialapp.pojo.User;
import java.util.List;
import java.util.Map;

public interface PostApiService {

    List<PostDTO> getPosts(Map<String, String> params);

    PostDTO getPostById(int id);

    PostDTO addOrUpdatePost(Post postFromRequest, User currentUser);

    boolean deletePost(int postId, User currentUser);

    List<Comment> getCommentsByPostId(int postId);

    PostDTO toggleCommentLock(Integer postId, User currentUser);

    List<Post> getPostsByUserId(int userId);
}

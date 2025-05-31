package com.socialapp.service;

import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import com.socialapp.pojo.User;
import java.util.List;
import java.util.Map;

/**
 *
 * @author DELL G15
 */
public interface PostService {

    List<Post> getPosts(Map<String, String> params);

    Post getPostById(int id);

    Post addOrUpdatePost(Post post);

    List<Post> getPostsByUserId(int userId);

    List<Comment> getCommentsByPostId(int postId);
    
    int countPostsCreatedToday();
    
    void deletePost(int postId, User currentUser);
    
    long countPosts(Map<String, String> params); 
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.repository;

import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import java.util.List;
import java.util.Map;

/**
 *
 * @author DELL G15
 */
public interface PostRepository {

    List<Post> getPosts(Map<String, String> params);

    List<Post> getPostsByUserId(int userId);

    Post getPostById(int id);

    Post addOrUpdatePost(Post post);

    void deletePost(int id);

    List<Comment> getCommentsByPostId(int postId);
    
     long countPosts();
     
     int countPostsCreatedToday();

}

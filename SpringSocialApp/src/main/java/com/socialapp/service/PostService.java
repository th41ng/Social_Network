/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.service;

import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
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

    void deletePost(int id);

    List<Post> getPostsByUserId(int userId);

    List<Comment> getCommentsByPostId(int postId);
    
    int countPostsCreatedToday();

}

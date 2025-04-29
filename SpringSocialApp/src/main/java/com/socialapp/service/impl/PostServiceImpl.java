/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.service.impl;

import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import com.socialapp.repository.PostRepository;
import com.socialapp.service.PostService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author DELL G15
 */
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Override
    public List<Post> getPosts(Map<String, String> params) {
        return this.postRepository.getPosts(params);
    }

    @Override
    public Post getPostById(int id) {
        return this.postRepository.getPostById(id);
    }

    @Override
    public Post addOrUpdatePost(Post post) {
        return this.postRepository.addOrUpdatePost(post);
    }

    @Override
    public void deletePost(int id) {
        this.postRepository.deletePost(id);
    }

    @Override
    public List<Post> getPostsByUserId(int userId) {
        return this.postRepository.getPostsByUserId(userId);
    }

    @Override
    public List<Comment> getCommentsByPostId(int postId) {
        return this.postRepository.getCommentsByPostId(postId);
    }

}

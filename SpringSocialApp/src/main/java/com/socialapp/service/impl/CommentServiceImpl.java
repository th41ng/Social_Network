/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.service.impl;

import com.socialapp.pojo.Comment;
import com.socialapp.repository.CommentRepository;
import com.socialapp.service.CommentService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author DELL G15
 */
@Service
public class CommentServiceImpl implements CommentService{

     @Autowired
    private CommentRepository commentRepository;

    @Override
    public List<Comment> getComments(Map<String, String> params) {
        return this.commentRepository.getComments(params);
    }

    @Override
    public Comment getCommentById(int id) {
        return this.commentRepository.getCommentById(id);
    }

    @Override
    public Comment addOrUpdateComment(Comment comment) {
        return this.commentRepository.addOrUpdateComment(comment);
    }

    @Override
    public void deleteComment(int id) {
        this.commentRepository.deleteComment(id);
    }

    @Override
    public List<Comment> getCommentsByPostId(int postId) {
        return this.commentRepository.getCommentsByPostId(postId);
    }
    
}

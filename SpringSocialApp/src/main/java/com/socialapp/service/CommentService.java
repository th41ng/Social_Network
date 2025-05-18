/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.service;

import com.socialapp.pojo.Comment;
import java.util.List;
import java.util.Map;

/**
 *
 * @author DELL G15
 */
public interface CommentService {

    List<Comment> getComments(Map<String, String> params);

    Comment getCommentById(int id);

    Comment addOrUpdateComment(Comment comment);

    void deleteComment(int id);

    List<Comment> getCommentsByPostId(int postId);
    
    Comment createComment(int postId, int userId, String content); 
}

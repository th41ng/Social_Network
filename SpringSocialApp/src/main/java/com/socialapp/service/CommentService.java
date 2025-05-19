/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.service;

import com.socialapp.dto.CommentDTO;
import com.socialapp.pojo.Comment;
import com.socialapp.pojo.User;
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
    
// === PHƯƠNG THỨC MỚI CHO SỬA VÀ XÓA CÓ KIỂM TRA QUYỀN ===
    /**
     * Updates a comment's content after checking ownership.
     * @param commentId ID of the comment to update.
     * @param content New content for the comment.
     * @param currentUser The user performing the update.
     * @return CommentDTO of the updated comment.
     * @throws jakarta.persistence.EntityNotFoundException if comment not found.
     * @throws SecurityException if user is not the owner.
     */
    CommentDTO updateComment(Integer commentId, String content, User currentUser);

    /**
     * Soft deletes a comment after checking ownership.
     * @param commentId ID of the comment to delete.
     * @param currentUser The user performing the deletion.
     * @return true if deletion was successful.
     * @throws jakarta.persistence.EntityNotFoundException if comment not found.
     * @throws SecurityException if user is not the owner.
     */
    boolean deleteComment(Integer commentId, User currentUser);}
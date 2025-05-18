/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.repository;

import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import com.socialapp.pojo.Reaction;
import com.socialapp.pojo.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author DELL G15
 */
public interface ReactionRepository {

    List<Reaction> getReactions(Map<String, String> params);

    Reaction getReactionById(int id);

    Reaction addOrUpdateReaction(Reaction reaction);

    void deleteReaction(int id);

    List<Reaction> getReactionsByPostId(int postId);

    List<Reaction> getReactionsByCommentId(int commentId);

    Map<String, Long> countReactionsByPostId(int postId);

    Map<String, Long> countReactionsByCommentId(int commentId);

    // Phương thức mới để tìm reaction cụ thể
    Optional<Reaction> findByUserAndPost(User user, Post post);

    Optional<Reaction> findByUserAndComment(User user, Comment comment);

}

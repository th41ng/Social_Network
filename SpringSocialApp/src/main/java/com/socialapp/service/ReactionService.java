/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.service;

import com.socialapp.pojo.Reaction;
import java.util.List;
import java.util.Map;

/**
 *
 * @author DELL G15
 */
public interface ReactionService {

    Reaction addOrUpdateReaction(Reaction reaction);

    void deleteReaction(int id);

    List<Reaction> getReactionsByPostId(int postId);

    List<Reaction> getReactionsByCommentId(int commentId);

    // Trả về Map<String, Long> là số lượng reactions mới của đối tượng (post hoặc comment)
    Map<String, Long> handlePostReaction(Integer postId, Integer userId, String reactionType);

    Map<String, Long> handleCommentReaction(Integer commentId, Integer userId, String reactionType);

    Map<String, Long> countReactionsByPostId(int postId);

    Map<String, Long> countReactionsByCommentId(int commentId);

}

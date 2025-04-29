/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.service;

import com.socialapp.pojo.Reaction;
import java.util.List;

/**
 *
 * @author DELL G15
 */
public interface ReactionService {

    Reaction addOrUpdateReaction(Reaction reaction);

    void deleteReaction(int id);

    List<Reaction> getReactionsByPostId(int postId);

    List<Reaction> getReactionsByCommentId(int commentId);
}

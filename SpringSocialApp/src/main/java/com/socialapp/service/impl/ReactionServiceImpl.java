/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.service.impl;

import com.socialapp.pojo.Reaction;
import com.socialapp.repository.ReactionRepository;
import com.socialapp.service.ReactionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author DELL G15
 */
@Service
public class ReactionServiceImpl implements ReactionService{

    @Autowired
    private ReactionRepository reactionRepository;

    @Override
    public Reaction addOrUpdateReaction(Reaction reaction) {
        return this.reactionRepository.addOrUpdateReaction(reaction);
    }

    @Override
    public void deleteReaction(int id) {
        this.reactionRepository.deleteReaction(id);
    }

    @Override
    public List<Reaction> getReactionsByPostId(int postId) {
        return this.reactionRepository.getReactionsByPostId(postId);
    }

    @Override
    public List<Reaction> getReactionsByCommentId(int commentId) {
        return this.reactionRepository.getReactionsByCommentId(commentId);
    }
    
}

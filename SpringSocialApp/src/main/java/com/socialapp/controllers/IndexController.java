/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;

import com.socialapp.pojo.Comment;
import com.socialapp.service.CategoryService;
import com.socialapp.service.PostService;
import com.socialapp.service.ReactionService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author DELL G15
 */
@Controller
@ControllerAdvice
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PostService postService;

    @Autowired
    private ReactionService reactionService;

    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("categories", this.categoryService.getCategories());
    }

@RequestMapping("/")
public String index(@RequestParam Map<String, String> params, Model model) {
    var posts = this.postService.getPosts(params);
    Map<Integer, List<Comment>> commentsMap = new HashMap<>();
    Map<Integer, Map<String, Long>> postReactionsMap = new HashMap<>();
    Map<Integer, Map<String, Long>> commentReactionsMap = new HashMap<>(); 

    for (var post : posts) {
        var comments = this.postService.getCommentsByPostId(post.getPostId());
        commentsMap.put(post.getPostId(), comments);

        //  Thống kê reaction cho từng comment
        for (var comment : comments) {
            commentReactionsMap.put(comment.getCommentId(),
                this.reactionService.countReactionsByCommentId(comment.getCommentId()));
        }

        //  Thống kê reaction cho post
        postReactionsMap.put(post.getPostId(),
            this.reactionService.countReactionsByPostId(post.getPostId()));
    }

    model.addAttribute("posts", posts);
    model.addAttribute("commentsMap", commentsMap);
    model.addAttribute("reactionsMap", postReactionsMap);
    model.addAttribute("commentReactionsMap", commentReactionsMap); 

    return "index";
}

}

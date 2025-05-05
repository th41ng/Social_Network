/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;

import com.socialapp.pojo.Comment;
import com.socialapp.service.CategoryService;
import com.socialapp.service.EventNotificationService;
import com.socialapp.service.EventService;
import com.socialapp.service.PostService;
import com.socialapp.service.ReactionService;
import com.socialapp.service.SurveyService;
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

    @Autowired
    private EventNotificationService EventNotificationService;

    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("categories", this.categoryService.getCategories());
    }

    @RequestMapping("/")
    public String index(@RequestParam(value = "categoryId", required = false) Integer categoryId, Model model) {
        if (categoryId != null) {
            switch (categoryId) {
                case 3: // Events
                    model.addAttribute("eventNotifications", EventNotificationService.getNotifications(Map.of()));
                    return "event_management";

                case 2: // Posts
                    var posts = postService.getPosts(Map.of());
                    Map<Integer, List<Comment>> commentsMap = new HashMap<>();
                    Map<Integer, Map<String, Long>> postReactionsMap = new HashMap<>();
                    Map<Integer, Map<String, Long>> commentReactionsMap = new HashMap<>();

                    for (var post : posts) {
                        var comments = postService.getCommentsByPostId(post.getPostId());
                        commentsMap.put(post.getPostId(), comments);

                        for (var comment : comments) {
                            commentReactionsMap.put(comment.getCommentId(),
                                    reactionService.countReactionsByCommentId(comment.getCommentId()));
                        }

                        postReactionsMap.put(post.getPostId(),
                                reactionService.countReactionsByPostId(post.getPostId()));
                    }

                    model.addAttribute("posts", posts);
                    model.addAttribute("commentsMap", commentsMap);
                    model.addAttribute("reactionsMap", postReactionsMap);
                    model.addAttribute("commentReactionsMap", commentReactionsMap);

                    return "post_management";
            }
        }

        // Mặc định
        var posts = postService.getPosts(Map.of());
        Map<Integer, List<Comment>> commentsMap = new HashMap<>();
        Map<Integer, Map<String, Long>> postReactionsMap = new HashMap<>();
        Map<Integer, Map<String, Long>> commentReactionsMap = new HashMap<>();

        for (var post : posts) {
            var comments = postService.getCommentsByPostId(post.getPostId());
            commentsMap.put(post.getPostId(), comments);

            for (var comment : comments) {
                commentReactionsMap.put(comment.getCommentId(),
                        reactionService.countReactionsByCommentId(comment.getCommentId()));
            }

            postReactionsMap.put(post.getPostId(),
                    reactionService.countReactionsByPostId(post.getPostId()));
        }

        model.addAttribute("posts", posts);
        model.addAttribute("commentsMap", commentsMap);
        model.addAttribute("reactionsMap", postReactionsMap);
        model.addAttribute("commentReactionsMap", commentReactionsMap);

        return "index";
    }

}

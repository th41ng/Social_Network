package com.socialapp.controllers;

import com.socialapp.pojo.Comment;
import com.socialapp.service.PostService;
import com.socialapp.service.ReactionService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private ReactionService reactionService;

    @GetMapping
    public String listPosts(@RequestParam Map<String, String> params, Model model) {
        var posts = postService.getPosts(params);

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
        model.addAttribute("params", params);

        return "post_management";
    }


}

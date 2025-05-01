/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;

import com.socialapp.pojo.Comment;
import com.socialapp.service.CategoryService;
import com.socialapp.service.PostService;
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

    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("categories", this.categoryService.getCategories());
    }

    @RequestMapping("/")
    public String index(@RequestParam Map<String, String> params, Model model) {
        var posts = this.postService.getPosts(params);
        Map<Integer, List<Comment>> commentsMap = new HashMap<>();

        for (var post : posts) {
            var comments = this.postService.getCommentsByPostId(post.getPostId());
            commentsMap.put(post.getPostId(), comments);
        }

        model.addAttribute("posts", posts);
        model.addAttribute("commentsMap", commentsMap);

        return "index";
    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;

import com.socialapp.service.PostService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author DELL G15
 */
@Controller
public class AdminController {

    @Autowired
    private PostService postService;

    @RequestMapping("/posts")
    public String postList(@RequestParam Map<String, String> params, Model model) {
        model.addAttribute("posts", postService.getPosts(params));
        model.addAttribute("params", params); // để hỗ trợ tìm kiếm, lọc
        return "post_management"; 
    }

    @PostMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable("id") int id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }
}

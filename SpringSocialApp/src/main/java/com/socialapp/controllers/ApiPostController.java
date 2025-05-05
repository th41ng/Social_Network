/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;
import com.socialapp.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author DELL G15
 */
@RestController
@RequestMapping("/api")
public class ApiPostController {

    @Autowired
    private PostService postService;

    private static final Logger logger = LoggerFactory.getLogger(ApiPostController.class);

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable("postId") int id) {
        try {
            logger.info("Đang xoá bài viết với ID: {}", id);
            postService.deletePost(id);
            logger.info("Đã xoá thành công bài viết với ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Lỗi khi xoá bài viết ID {}:", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
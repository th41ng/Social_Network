/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;
import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import com.socialapp.service.PostService;
import java.util.List;
import java.util.Map;
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
@CrossOrigin
public class ApiPostController {

    @Autowired
    private PostService postService;

    private static final Logger logger = LoggerFactory.getLogger(ApiPostController.class);
    
    // GET: Danh sách bài viết 
    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getPosts(@RequestParam Map<String, String> params) {
        return new ResponseEntity<>(postService.getPosts(params), HttpStatus.OK);
    }

    // GET: Chi tiết 1 bài viết
    @GetMapping("/posts/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable("postId") int id) {
        return new ResponseEntity<>(postService.getPostById(id), HttpStatus.OK);
    }

    // POST: Thêm bài viết
    @PostMapping("/posts")
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post saved = postService.addOrUpdatePost(post);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // PUT: Cập nhật bài viết
    @PutMapping("/posts/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable("postId") int id, @RequestBody Post post) {
        post.setPostId(id);  // gán ID vào post để update đúng
        Post updated = postService.addOrUpdatePost(post);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
    
    
    // DELETE: Xóa bài viết
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
    
     // GET: Lấy comment theo bài viết 
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable("postId") int id) {
        return new ResponseEntity<>(postService.getCommentsByPostId(id), HttpStatus.OK);
    }
}
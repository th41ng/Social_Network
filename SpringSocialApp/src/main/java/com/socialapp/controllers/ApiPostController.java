package com.socialapp.controllers;

import com.socialapp.dto.PostDTO;
import com.socialapp.pojo.Post;
import com.socialapp.service.PostApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiPostController {

    @Autowired
    private PostApiService postApiService;


    @GetMapping("/posts")
    public ResponseEntity<List<PostDTO>> getPosts(@RequestParam Map<String, String> params) {
        List<PostDTO> posts = postApiService.getPosts(params);
        return ResponseEntity.ok(posts);
    }

  
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable("postId") int id) {
        PostDTO postDTO = postApiService.getPostById(id);
        if (postDTO != null) {
            return ResponseEntity.ok(postDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


     
    @PostMapping("/posts")
    public ResponseEntity<PostDTO> addOrUpdatePost(@RequestBody Post post) {
        PostDTO postDTO = postApiService.addOrUpdatePost(post);
        return ResponseEntity.ok(postDTO);
    }

 
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable("postId") int id) {
        postApiService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
    
    
    
}

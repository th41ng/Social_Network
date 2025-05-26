package com.socialapp.controllers;

import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import com.socialapp.pojo.User; 
import com.socialapp.service.PostService;
import com.socialapp.service.ReactionService;
import com.socialapp.service.UserService; 

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; 
import org.springframework.security.core.userdetails.UserDetails; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException; 
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/posts")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class); 

    @Autowired
    private PostService postService;

    @Autowired
    private ReactionService reactionService;

    @Autowired
    private UserService userService; 

    @GetMapping
    public String listPosts(@RequestParam Map<String, String> params, Model model) {
        logger.info("Yêu cầu danh sách bài viết với params: {}", params);
        var posts = postService.getPosts(params);

        Map<Integer, List<Comment>> commentsMap = new HashMap<>();
        Map<Integer, Map<String, Long>> postReactionsMap = new HashMap<>();
        Map<Integer, Map<String, Long>> commentReactionsMap = new HashMap<>();

        if (posts != null) { 
            for (var post : posts) {
                if (post != null && post.getPostId() != null) { 
                    var comments = postService.getCommentsByPostId(post.getPostId());
                    commentsMap.put(post.getPostId(), comments);

                    if (comments != null) { 
                        for (var comment : comments) {
                            if (comment != null && comment.getCommentId() != null) { 
                                commentReactionsMap.put(comment.getCommentId(),
                                        reactionService.countReactionsByCommentId(comment.getCommentId()));
                            }
                        }
                    }
                    postReactionsMap.put(post.getPostId(),
                            reactionService.countReactionsByPostId(post.getPostId()));
                }
            }
        }

        model.addAttribute("posts", posts);
        model.addAttribute("commentsMap", commentsMap);
        model.addAttribute("reactionsMap", postReactionsMap);
        model.addAttribute("commentReactionsMap", commentReactionsMap);
        model.addAttribute("params", params);

        return "post_management";
    }

   
    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable("postId") int postId, 
                             Authentication authentication, // Thêm Authentication để lấy người dùng hiện tại
                             RedirectAttributes redirectAttributes) { // Thêm RedirectAttributes để gửi thông báo

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Người dùng chưa xác thực cố gắng xóa bài viết ID: {}", postId);
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để thực hiện hành động này.");
            return "redirect:/Users/login"; 
        }

        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username);

        if (currentUser == null) {
            logger.error("Không thể tìm thấy thông tin người dùng cho username: {} khi cố gắng xóa bài viết ID: {}", username, postId);
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin người dùng hợp lệ để thực hiện hành động này.");
            return "redirect:/posts";
        }

        try {
            logger.info("Người dùng '{}' (ID: {}) yêu cầu xóa bài viết ID: {}", currentUser.getUsername(), currentUser.getId(), postId);
            // Gọi phương thức deletePost trong service đã có logic kiểm tra quyền
            postService.deletePost(postId, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa bài viết (ID: " + postId + ") thành công!");
        } catch (ResponseStatusException ex) {
            // Bắt các lỗi như NOT_FOUND, FORBIDDEN từ service
            logger.warn("Lỗi khi người dùng {} (ID: {}) xóa bài viết ID {}: {} - {}", 
                        currentUser.getUsername(), currentUser.getId(), postId, ex.getStatusCode(), ex.getReason());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getReason()); // Hiển thị lý do lỗi cho người dùng
        } catch (Exception ex) {
            // Bắt các lỗi không mong muốn khác
            logger.error("Lỗi không xác định khi người dùng {} (ID: {}) xóa bài viết ID {}: {}", 
                         currentUser.getUsername(), currentUser.getId(), postId, ex.getMessage(), ex);
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi không mong muốn khi cố gắng xóa bài viết.");
        }
        return "redirect:/posts"; 
    }
}
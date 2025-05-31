package com.socialapp.controllers;

import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import com.socialapp.pojo.User;
import com.socialapp.service.CommentService; 
import com.socialapp.service.PostService;
import com.socialapp.service.ReactionService;
import com.socialapp.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; 
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/posts")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    private static final int PAGE_SIZE = 5;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService; 

    @Autowired
    private ReactionService reactionService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listPosts(@RequestParam Map<String, String> params, Model model) {
        logger.info("Yêu cầu danh sách bài viết với params: {}", params);

        int currentPage = 1;
        String pageParam = params.get("page");

        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) {
                    currentPage = 1;
                }
            } catch (NumberFormatException e) {
                logger.warn("Tham số 'page' không hợp lệ: '{}'. Sử dụng trang 1.", pageParam);
            }
        }
        params.put("page", String.valueOf(currentPage));

        List<Post> posts = Collections.emptyList();
        long totalPosts = 0;
        int counter = 0;

        try {
            totalPosts = postService.countPosts(params);

            if (totalPosts > 0) {
                counter = (int) Math.ceil((double) totalPosts / PAGE_SIZE);
                if (currentPage > counter && counter > 0) { 
                    currentPage = counter;
                    params.put("page", String.valueOf(currentPage));
                } else if (counter == 0) { 
                     currentPage = 1;
                }
            } else {
                 currentPage = 1;
            }
            
            posts = postService.getPosts(params);

        } catch (Exception e) {
            logger.error("Lỗi khi xử lý dữ liệu bài viết: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi tải dữ liệu bài viết.");
            model.addAttribute("posts", posts);
            model.addAttribute("commentsMap", new HashMap<>());
            model.addAttribute("reactionsMap", new HashMap<>());
            model.addAttribute("commentReactionsMap", new HashMap<>());
            model.addAttribute("params", params);
            model.addAttribute("counter", 0);
            model.addAttribute("currentPage", 1);
            return "post_management";
        }

        Map<Integer, List<Comment>> commentsMap = new HashMap<>();
        Map<Integer, Map<String, Long>> postReactionsMap = new HashMap<>();
        Map<Integer, Map<String, Long>> commentReactionsMap = new HashMap<>();

        if (posts != null) {
            for (var post : posts) {
                if (post != null && post.getPostId() != null) {
                
                    var comments = commentService.getCommentsByPostId(post.getPostId()); 
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
        model.addAttribute("counter", counter);
        model.addAttribute("currentPage", currentPage);

        logger.info("PostController: Displaying posts for page: {}, total posts: {}, total pages (counter): {}", currentPage, totalPosts, counter);
        return "post_management";
    }

    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable("postId") int postId,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes,
                             @RequestParam Map<String, String> params) { 

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
            return createRedirectUrlWithParams("/posts", params); // ++ Sử dụng helper để redirect với params
        }

        try {
            logger.info("Người dùng '{}' (ID: {}) yêu cầu xóa bài viết ID: {}", currentUser.getUsername(), currentUser.getId(), postId);
            postService.deletePost(postId, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa bài viết (ID: " + postId + ") thành công!");
        } catch (ResponseStatusException ex) {
            logger.warn("Lỗi khi người dùng {} (ID: {}) xóa bài viết ID {}: {} - {}",
                    currentUser.getUsername(), currentUser.getId(), postId, ex.getStatusCode(), ex.getReason());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getReason());
        } catch (Exception ex) {
            logger.error("Lỗi không xác định khi người dùng {} (ID: {}) xóa bài viết ID {}: {}",
                    currentUser.getUsername(), currentUser.getId(), postId, ex.getMessage(), ex);
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi không mong muốn khi cố gắng xóa bài viết.");
        }
        return createRedirectUrlWithParams("/posts", params); 
    }

    
    private String createRedirectUrlWithParams(String baseUrl, Map<String, String> params) {
        StringBuilder redirectUrl = new StringBuilder(baseUrl);
        if (params != null && !params.isEmpty()) {
            redirectUrl.append("?");
            params.forEach((key, value) -> {
                if (value != null && !value.isEmpty()) {
                    
                    if ("page".equals(key) || "content".equals(key) || "fromDate".equals(key) || "toDate".equals(key)) {
                         redirectUrl.append(key).append("=").append(value).append("&");
                    }
                }
            });
            if (redirectUrl.charAt(redirectUrl.length() - 1) == '&') {
                redirectUrl.deleteCharAt(redirectUrl.length() - 1);
            }
        }
        return "redirect:" + redirectUrl.toString();
    }
    
    
    @GetMapping("/{postId}/manage-comments")
    public String showManagePostCommentsPage(@PathVariable("postId") int postId, Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() ) {
        
            logger.warn("Người dùng chưa xác thực hoặc không có quyền truy cập trang quản lý comment cho bài viết ID: {}", postId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập trang này.");
        }
        
        Post post = postService.getPostById(postId);
        if (post == null) {
            logger.warn("Không tìm thấy bài viết với ID: {} để quản lý comment.", postId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bài viết không tồn tại.");
        }

        List<Comment> comments = commentService.getCommentsByPostId(postId); 

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        return "manage_post_comments"; 
    }

    @PostMapping("/{postId}/comments/{commentId}/delete")
    public String deleteCommentFromPostManagement(@PathVariable("postId") int postId,
                                                 @PathVariable("commentId") int commentId,
                                                 Authentication authentication,
                                                 RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated() ) {
         
            logger.warn("Người dùng chưa xác thực hoặc không có quyền xóa comment ID: {} cho bài viết ID: {}", commentId, postId);
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền thực hiện hành động này.");
            return "redirect:/posts/" + postId + "/manage-comments";
        }
        
    

        try {
            commentService.deleteComment(commentId); 
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa bình luận (ID: " + commentId + ") thành công!");
            logger.info("Comment ID {} của post ID {} đã được xóa bởi quản trị viên.", commentId, postId);
        } catch (Exception e) {
            logger.error("Lỗi khi xóa bình luận ID {}: {}", commentId, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa bình luận: " + e.getMessage());
        }
        return "redirect:/posts/" + postId + "/manage-comments";
    }

}
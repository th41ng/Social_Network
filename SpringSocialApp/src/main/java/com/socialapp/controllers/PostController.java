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
// Bỏ import UserDetails nếu không dùng trực tiếp ở đây
// import org.springframework.security.core.userdetails.UserDetails; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections; // Thêm import này
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/posts")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    // Định nghĩa PAGE_SIZE cho posts. Đảm bảo PostRepository của bạn cũng dùng PAGE_SIZE này.
    private static final int PAGE_SIZE = 5; 

    @Autowired
    private PostService postService;

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
                // currentPage vẫn là 1 nếu có lỗi
            }
        }
        // Đảm bảo params luôn có "page" cho repository và để giữ lại trên URL
        params.put("page", String.valueOf(currentPage));

        List<Post> posts = Collections.emptyList();
        long totalPosts = 0;
        int counter = 0; // Sẽ là tổng số trang

        try {
            // Lấy tổng số posts TRƯỚC để tính tổng số trang
            totalPosts = postService.countPosts(params); 

            if (totalPosts > 0) {
                counter = (int) Math.ceil((double) totalPosts / PAGE_SIZE);
                // Xử lý trường hợp currentPage nằm ngoài phạm vi hợp lệ
                if (currentPage > counter) {
                    currentPage = counter; // Chuyển về trang cuối cùng hợp lệ
                    params.put("page", String.valueOf(currentPage)); // Cập nhật lại params
                }
            } else {
                // Không có bài viết nào, counter = 0, currentPage có thể để là 1
                currentPage = 1; // Mặc định về trang 1 nếu không có bài viết
            }
            
            // Lấy danh sách posts cho trang hiện tại (đã được điều chỉnh nếu cần)
            posts = postService.getPosts(params);

        } catch (Exception e) {
            logger.error("Lỗi khi xử lý dữ liệu bài viết: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi tải dữ liệu bài viết.");
            // Gán giá trị mặc định cho các thuộc tính model cần thiết để view không lỗi
            model.addAttribute("posts", posts); // posts là emptyList
            model.addAttribute("commentsMap", new HashMap<>());
            model.addAttribute("reactionsMap", new HashMap<>());
            model.addAttribute("commentReactionsMap", new HashMap<>());
            model.addAttribute("params", params); 
            model.addAttribute("counter", 0);
            model.addAttribute("currentPage", 1);
            return "post_management";
        }

        // Logic lấy comments và reactions giữ nguyên, áp dụng cho danh sách 'posts' đã được phân trang
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
        model.addAttribute("params", params); // params này chứa cả page và các filter params khác
        model.addAttribute("counter", counter); // Tổng số trang
        model.addAttribute("currentPage", currentPage); // Trang hiện tại

        logger.info("PostController: Displaying posts for page: {}, total posts: {}, total pages (counter): {}", currentPage, totalPosts, counter);
        return "post_management";
    }

    
    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable("postId") int postId, 
                             Authentication authentication, 
                             RedirectAttributes redirectAttributes) { 

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
            return "redirect:/posts"; // Redirect đơn giản, không kèm filter params vì không có trong method signature
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
        return "redirect:/posts"; // Redirect đơn giản
    }
}
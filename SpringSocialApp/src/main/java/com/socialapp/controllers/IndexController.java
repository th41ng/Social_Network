package com.socialapp.controllers;

import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Survey;
import com.socialapp.service.CategoryService;
import com.socialapp.service.EventNotificationService;
import com.socialapp.service.EventService;
import com.socialapp.service.PostService;
import com.socialapp.service.ReactionService;
import com.socialapp.service.SurveyQuestionService;
import com.socialapp.service.SurveyResponseService;
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

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private SurveyQuestionService surveyQuestionService;

    @Autowired
    private SurveyResponseService surveyResponseService;

    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("categories", this.categoryService.getCategories());
    }

    @RequestMapping("/")
    public String index(@RequestParam(value = "categoryId", required = false) Integer categoryId, Model model) {
        Map<String, String> params = new HashMap<>();
        model.asMap().forEach((k, v) -> {
            if (v instanceof String) {
                params.put(k, (String) v);
            }
        });
        model.addAttribute("params", params); // Dùng chung params để lọc kết quả

        if (categoryId != null) {
            switch (categoryId) {
                case 3: // Thông báo
                    model.addAttribute("notification", EventNotificationService.getNotifications(params));
                    return "notification_management";

                case 4: // Surveys
                    List<Survey> surveys = surveyService.getSurveys(params);
                    model.addAttribute("surveys", surveys);
                    return "survey_management";  // Hiển thị trang quản lý khảo sát

                case 2: // Posts
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

                    return "post_management";
            }
        }

        // Mặc định: hiển thị tất cả post
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

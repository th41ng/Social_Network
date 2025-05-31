package com.socialapp.controllers;

import static com.mysql.cj.conf.PropertyKey.logger;
import com.socialapp.pojo.Comment;
import com.socialapp.pojo.PeriodicSummaryStats;
import com.socialapp.pojo.Survey;
import com.socialapp.service.CategoryService;
import com.socialapp.service.EventNotificationService;
import com.socialapp.service.EventService;
import com.socialapp.service.PlatformStatsService;
import com.socialapp.service.PostService;
import com.socialapp.service.ReactionService;
import com.socialapp.service.SurveyQuestionService;
import com.socialapp.service.SurveyResponseService;
import com.socialapp.service.SurveyService;
import com.socialapp.service.UserService;
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

    private static final int PAGE_SIZE = 5;

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
    private UserService userService;



    @Autowired
    private PlatformStatsService platformStatsService;

    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("categories", this.categoryService.getCategories());
    }

    @RequestMapping("/")
    public String index(@RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model) {
        Map<String, String> params = new HashMap<>();

        
        model.asMap().forEach((k, v) -> {
            if (v instanceof String string) {
                params.put(k, string);
            }
        });
       
        params.put("page", String.valueOf(page));
        params.put("categoryId", String.valueOf(categoryId));

        model.addAttribute("params", params); 
        model.addAttribute("currentPage", page); 

        if (categoryId != null) {
            switch (categoryId) {
                case 5 -> {
                    //User
                    var users = userService.getAllUsers(params);
                    long totalUser = userService.countUsers();
                    int totalPage = (int) Math.ceil((double) totalUser / PAGE_SIZE);
                    
                    model.addAttribute("users", users);
                    model.addAttribute("totalUsers", totalUser);
                    model.addAttribute("currentPage", page);
                    model.addAttribute("totalPage", totalPage);
                    model.addAttribute("params", params); 
                    model.addAttribute("categoryId", categoryId);
                    return "user_management";
                }
                case 3 -> {
                    // Thông báo
                    var noti = EventNotificationService.getNotifications(params);
                    long totalNoti = EventNotificationService.countNotis(); 
                    int totalNotiPage = (int) Math.ceil((double) totalNoti / PAGE_SIZE);
                    model.addAttribute("totalUsers", totalNoti);
                    model.addAttribute("currentPage", page);
                    model.addAttribute("totalPages", totalNotiPage); 
                    model.addAttribute("notification", noti);
                    return "notification_management";
                }

                case 4 -> {
                    // Surveys
 
                    String pageParamSurvey = params.get("page");
                    int surveyPage = (pageParamSurvey == null || pageParamSurvey.trim().isEmpty()) ? 1 : Integer.parseInt(pageParamSurvey);
                    if (surveyPage < 1) {
                        surveyPage = 1;
                    }
                    
                    Map<String, String> surveyParams = new HashMap<>(params); 
                    surveyParams.put("page", String.valueOf(surveyPage));

                    List<Survey> surveys = surveyService.getSurveys(surveyParams);
                    long totalSurveys = surveyService.countSurveys(surveyParams);
                    
                    int pageSizeSurveys = com.socialapp.repository.impl.SurveyRepositoryImpl.PAGE_SIZE; 
                    int totalSurveyPages = (int) Math.ceil((double) totalSurveys / pageSizeSurveys);

                    model.addAttribute("surveys", surveys);
                    model.addAttribute("params", surveyParams); 
                    model.addAttribute("currentPage", surveyPage);
                    model.addAttribute("totalPages", totalSurveyPages);
                    return "survey_management";
                }

                case 2 -> {
                    // Posts
                    var posts = postService.getPosts(params);
                    long totalPosts = postService.countPosts(params);
                    int counter = (int) Math.ceil((double) totalPosts / PAGE_SIZE);
                    
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
                    model.addAttribute("counter", counter);
                    
                    return "post_management";
                }

                case 6 -> {
                    // Thống kê nền tảng
                    platformStatsService.generateDailySummary();
                    model.addAttribute("stats", platformStatsService.getAllSummaries());

                    List<PeriodicSummaryStats> periodicStats = platformStatsService.getAllPeriodicSummaries();
                    model.addAttribute("periodicStats", periodicStats);

                    return "stats_management";
                }

            }
        }

       
        var posts = postService.getPosts(params); 
        long totalPosts = postService.countPosts(params); 
        int counter = (int) Math.ceil((double) totalPosts / PAGE_SIZE); // Tính tổng số trang

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
        model.addAttribute("counter", counter);

        return "index";
    }
}

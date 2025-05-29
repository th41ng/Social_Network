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

    private static final int PAGE_SIZE = 10;

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
    private SurveyQuestionService surveyQuestionService;

    @Autowired
    private SurveyResponseService surveyResponseService;

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

        // Thêm các tham số từ request vào params
        model.asMap().forEach((k, v) -> {
            if (v instanceof String) {
                params.put(k, (String) v);
            }
        });
        // Đảm bảo param 'page' luôn có trong map
        params.put("page", String.valueOf(page));
        params.put("categoryId", String.valueOf(categoryId));

        model.addAttribute("params", params); // Dùng chung params để lọc kết quả
        model.addAttribute("currentPage", page); // Truyền trang hiện tại cho frontend

        if (categoryId != null) {
            switch (categoryId) {
                case 5: //User
                    var users = userService.getAllUsers(params);
                    long totalUser = userService.countUsers(); // Lấy tổng số bài viết
                    int totalPage = (int) Math.ceil((double) totalUser / PAGE_SIZE); // Tính tổng số trang

                    model.addAttribute("users", users);
                    model.addAttribute("totalUsers", totalUser); // Tổng số người dùng
                    model.addAttribute("currentPage", page); // Trang hiện tại
                    model.addAttribute("totalPage", totalPage); // Tổng số trang
                    model.addAttribute("params", params); // Truyền params để dùng trong frontend
                    model.addAttribute("categoryId", categoryId);
                    return "user_management";
                case 3: // Thông báo
                    model.addAttribute("notification", EventNotificationService.getNotifications(params));
                    return "notification_management";

                case 4: // Surveys
                    // Lấy page từ params, đảm bảo params có "page"
                    String pageParamSurvey = params.get("page"); // params này là params của IndexController
                    int surveyPage = (pageParamSurvey == null || pageParamSurvey.trim().isEmpty()) ? 1 : Integer.parseInt(pageParamSurvey);
                    if (surveyPage < 1) {
                        surveyPage = 1;
                    }
                    // Tạo một Map params riêng cho surveyService nếu cần, hoặc dùng chung nếu cấu trúc params phù hợp
                    Map<String, String> surveyParams = new HashMap<>(params); // Sao chép params hiện tại
                    surveyParams.put("page", String.valueOf(surveyPage));

                    List<Survey> surveys = surveyService.getSurveys(surveyParams);
                    long totalSurveys = surveyService.countSurveys(surveyParams); // Đếm tổng số survey

                    int pageSizeSurveys = com.socialapp.repository.impl.SurveyRepositoryImpl.PAGE_SIZE; // Lấy PAGE_SIZE từ SurveyRepositoryImpl
                    int totalSurveyPages = (int) Math.ceil((double) totalSurveys / pageSizeSurveys);

                    model.addAttribute("surveys", surveys);
                    model.addAttribute("params", surveyParams); // Truyền params đã cập nhật (có page)
                    model.addAttribute("currentPage", surveyPage);
                    model.addAttribute("totalPages", totalSurveyPages);
                    return "survey_management";

                case 2: // Posts
                    var posts = postService.getPosts(params);
                    long totalPosts = postService.countPosts(params); // Đảm bảo countPosts nhận params
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
                    model.addAttribute("counter", counter); // Truyền tổng số trang cho frontend

                    return "post_management";

                case 6: // Thống kê nền tảng
                    platformStatsService.generateDailySummary(); // Đảm bảo có dữ liệu mỗi ngày
                    model.addAttribute("stats", platformStatsService.getAllSummaries());

                    List<PeriodicSummaryStats> periodicStats = platformStatsService.getAllPeriodicSummaries();
                    model.addAttribute("periodicStats", periodicStats);

                    return "stats_management";

            }
        }

        // Mặc định: hiển thị tất cả post
        var posts = postService.getPosts(params); // Truyền params để sử dụng phân trang
        long totalPosts = postService.countPosts(params); // Đảm bảo countPosts nhận params
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

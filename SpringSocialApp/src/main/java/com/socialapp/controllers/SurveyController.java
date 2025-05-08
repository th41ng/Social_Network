package com.socialapp.controllers;

import com.socialapp.pojo.Survey;
import com.socialapp.pojo.User;
import com.socialapp.service.CategoryService;
import com.socialapp.service.SurveyService;
import com.socialapp.service.UserService;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@ControllerAdvice
@RequestMapping("/surveys")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("categories", this.categoryService.getCategories());
    }

    // Hiển thị danh sách khảo sát
    @GetMapping
    public String listSurveys(@RequestParam Map<String, String> params, Model model) {
        List<Survey> surveys = this.surveyService.getSurveys(params);

        model.addAttribute("surveys", surveys);
        model.addAttribute("params", params);
        return "survey_management";
    }

    @GetMapping("/add")
    public String addSurveyForm(Model model) {
        model.addAttribute("survey", new Survey());
        return "survey";
    }

    // Dùng tạm do chưa có chức năng đăng nhập nên minh hong biết dược User_ID trong dây
    @PostMapping("/add")
    public String addOrUpdateSurvey(@ModelAttribute Survey survey) {
        User defaultAdmin = this.userService.getUserById(4);
        survey.setAdminId(defaultAdmin);

        this.surveyService.addOrUpdateSurvey(survey);
        return "redirect:/surveys";
    }

    @GetMapping("/{surveyId}")
    public String editSurveyForm(@PathVariable("surveyId") int id, Model model) {
        Survey s = this.surveyService.getSurveyById(id);
        model.addAttribute("survey", s);
        return "survey";
    }

    // Dùng tạm do chưa có chức năng đăng nhập nên minh hong biết dược User_ID trong dây
   @PostMapping("/save")
public String saveSurvey(@ModelAttribute Survey survey) {
    // Gán admin mặc định
    User defaultAdmin = this.userService.getUserById(4);
    survey.setAdminId(defaultAdmin);

    if (survey.getSurveyId() == null) {
        // Thêm mới khảo sát, set thời gian hiện tại
        survey.setCreatedAt(new Date());
    } else {
       
        survey.setCreatedAt(new Date()); // Cập nhật ngày tạo mới khi sửa
    }

    this.surveyService.addOrUpdateSurvey(survey);
    return "redirect:/surveys";
}

}

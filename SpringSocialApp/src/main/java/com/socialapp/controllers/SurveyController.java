package com.socialapp.controllers;

import com.socialapp.pojo.Survey;
import com.socialapp.pojo.User;
import com.socialapp.service.CategoryService;
import com.socialapp.service.SurveyService;
import com.socialapp.service.UserService;
import jakarta.persistence.EntityNotFoundException; // Quan trọng: Import đúng
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Import RedirectAttributes

import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@ControllerAdvice // Nếu bạn dùng @ControllerAdvice cho exception handling chung
@RequestMapping("/surveys")
public class SurveyController {

    private static final Logger logger = LoggerFactory.getLogger(SurveyController.class); // Thêm logger

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
        model.addAttribute("params", params); // Để giữ lại giá trị lọc trên form
        return "survey_management"; // Tên view cho trang quản lý khảo sát
    }

    // Hiển thị form thêm khảo sát mới
    @GetMapping("/add")
    public String addSurveyForm(Model model) {
        Survey newSurvey = new Survey();
        newSurvey.setIsActive(true); // Mặc định khảo sát mới là active
        model.addAttribute("survey", newSurvey);
        return "survey"; // Tên view cho form thêm/sửa survey (survey.html)
    }

    // Xóa @PostMapping("/add") này đi vì /save sẽ xử lý cả thêm mới và cập nhật
    // @PostMapping("/add")
    // public String addOrUpdateSurvey(@ModelAttribute Survey survey) {
    //     // Logic này sẽ được gộp vào /save
    // }

    // Hiển thị form sửa khảo sát
    @GetMapping("/{surveyId}")
    public String editSurveyForm(@PathVariable("surveyId") int id, Model model, RedirectAttributes redirectAttributes) {
        Survey survey = this.surveyService.getSurveyById(id);
        if (survey == null) {
            logger.warn("Attempted to edit non-existent survey with ID: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy khảo sát để chỉnh sửa.");
            return "redirect:/surveys";
        }
        model.addAttribute("survey", survey);
        return "survey"; // Tên view cho form thêm/sửa survey (survey.html)
    }

    // Xử lý lưu (thêm mới hoặc cập nhật) khảo sát
    @PostMapping("/save")
    public String saveSurvey(@ModelAttribute Survey survey, RedirectAttributes redirectAttributes) {
        // Tạm thời gán admin mặc định
        User defaultAdmin = this.userService.getUserById(4); // Cân nhắc lấy User từ Principal khi có Spring Security
        survey.setAdminId(defaultAdmin);

        try {
            if (survey.getSurveyId() == null) { // Thêm mới khảo sát
                survey.setCreatedAt(new Date()); // Đặt ngày tạo
                if (survey.getIsActive() == null) { // Nếu form không gửi (ví dụ: checkbox không được tick)
                    survey.setIsActive(true); // Mặc định là active khi tạo mới
                }
                this.surveyService.addOrUpdateSurvey(survey);
                redirectAttributes.addFlashAttribute("successMessage", "Thêm khảo sát mới thành công!");
                logger.info("New survey added with ID: {}", survey.getSurveyId());
            } else { // Cập nhật khảo sát hiện có
                Survey existingSurvey = surveyService.getSurveyById(survey.getSurveyId());
                if (existingSurvey == null) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy khảo sát để cập nhật.");
                    return "redirect:/surveys";
                }
                // Giữ lại ngày tạo gốc, không cập nhật lại ngày tạo khi sửa
                survey.setCreatedAt(existingSurvey.getCreatedAt());

                if (survey.getIsActive() == null) { // Nếu checkbox isActive không được tick khi submit form sửa
                    survey.setIsActive(false);
                }
                this.surveyService.addOrUpdateSurvey(survey);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật khảo sát thành công!");
                logger.info("Survey updated with ID: {}", survey.getSurveyId());
            }
        } catch (Exception e) {
            logger.error("Error saving survey (ID: {}): {}", survey.getSurveyId(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi lưu khảo sát.");
        }
        return "redirect:/surveys";
    }

    // Endpoint mới để bật/tắt trạng thái active của survey
    @PostMapping("/toggle-active/{surveyId}")
    public String toggleSurveyActiveState(@PathVariable("surveyId") int surveyId, RedirectAttributes redirectAttributes) {
        try {
            surveyService.toggleSurveyActiveState(surveyId);
            redirectAttributes.addFlashAttribute("successMessage", "Thay đổi trạng thái khảo sát thành công!");
            logger.info("Toggled active state for survey ID: {}", surveyId);
        } catch (EntityNotFoundException e) {
            logger.warn("Attempted to toggle active state for non-existent survey: {}", surveyId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy khảo sát.");
        } catch (Exception e) {
            logger.error("Error toggling active state for survey {}: {}", surveyId, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi thay đổi trạng thái khảo sát.");
        }
        return "redirect:/surveys";
    }
}
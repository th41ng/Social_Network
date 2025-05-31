package com.socialapp.controllers;

import com.socialapp.pojo.Survey;
import com.socialapp.pojo.User;
import com.socialapp.repository.impl.SurveyRepositoryImpl; 
import com.socialapp.service.CategoryService;
import com.socialapp.service.SurveyService;
import com.socialapp.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/surveys")
public class SurveyController {

    private static final Logger logger = LoggerFactory.getLogger(SurveyController.class);

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

    
    @GetMapping
    public String listSurveys(@RequestParam Map<String, String> params, Model model) {
        String pageParam = params.get("page");
        int page = (pageParam == null || pageParam.trim().isEmpty()) ? 1 : Integer.parseInt(pageParam);
        if (page < 1) {
            page = 1;
        }
      
        params.put("page", String.valueOf(page));

        List<Survey> surveys = this.surveyService.getSurveys(params);
        long totalSurveys = this.surveyService.countSurveys(params); 

       
        int pageSize = SurveyRepositoryImpl.PAGE_SIZE; 
        int totalPages = (int) Math.ceil((double) totalSurveys / pageSize);

        model.addAttribute("surveys", surveys);
        model.addAttribute("params", params);
        model.addAttribute("currentPage", page); 
        model.addAttribute("totalPages", totalPages); 
      

        logger.info("SurveyController: Displaying surveys for page: {}, total surveys: {}, total pages: {}", page, totalSurveys, totalPages);
        return "survey_management";
    }

  
    @GetMapping("/add")
    public String addSurveyForm(Model model) {
        Survey newSurvey = new Survey();
        newSurvey.setIsActive(true); 
        model.addAttribute("survey", newSurvey);
        return "survey";
    }

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
        return "survey";
    }

    @PostMapping("/save")
    public String saveSurvey(@ModelAttribute Survey survey, RedirectAttributes redirectAttributes, Authentication authentication) {
        try {
          
            if (authentication == null || !authentication.isAuthenticated()) {
                logger.warn("Người dùng chưa xác thực khi tạo hoặc chỉnh sửa khảo sát.");
                redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để thực hiện hành động này.");
                return "redirect:/Users/login"; 
            }

            String username = authentication.getName(); 
            User currentUser = userService.getUserByUsername(username); 

            if (currentUser == null) {
                logger.error("Không thể tìm thấy thông tin người dùng với username: {}", username);
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng hợp lệ.");
                return "redirect:/surveys"; 
            }

        
            survey.setAdminId(currentUser);

            if (survey.getSurveyId() == null) { 
                survey.setCreatedAt(new Date());
                if (survey.getIsActive() == null) {
                    survey.setIsActive(true); 
                }
                this.surveyService.addOrUpdateSurvey(survey);
                redirectAttributes.addFlashAttribute("successMessage", "Thêm khảo sát mới thành công!");
                logger.info("New survey added with ID: {}", survey.getSurveyId());
            } else { 
                Survey existingSurvey = surveyService.getSurveyById(survey.getSurveyId());
                if (existingSurvey == null) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy khảo sát để cập nhật.");
                    return "redirect:/surveys";
                }
              
                survey.setCreatedAt(existingSurvey.getCreatedAt());

              
                if (survey.getIsActive() == null) {
                    survey.setIsActive(existingSurvey.getIsActive());
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

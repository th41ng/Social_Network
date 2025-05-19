/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;

import com.socialapp.pojo.Survey; 
import com.socialapp.pojo.SurveyQuestion;
import com.socialapp.pojo.SurveyResponse;
import com.socialapp.service.SurveyQuestionService;
import com.socialapp.service.SurveyResponseService;
import com.socialapp.service.SurveyService; 

import java.util.ArrayList; 
import java.util.LinkedHashMap; 
import java.util.List;
import java.util.Map; 

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory; // Thêm import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 

/**
 *
 * @author DELL G15
 */
@Controller
@RequestMapping("/responses")
public class SurveyResponseController {

    private static final Logger logger = LoggerFactory.getLogger(SurveyResponseController.class); 

    @Autowired
    private SurveyResponseService surveyResponseService;

    @Autowired
    private SurveyQuestionService surveyQuestionService;

    @Autowired
    private SurveyService surveyService; 

    // Hiển thị các phản hồi cho một câu hỏi cụ thể
    @GetMapping("/{questionId}")
    public String listResponsesForQuestion(@PathVariable("questionId") int questionId, Model model, RedirectAttributes redirectAttributes) { // Đổi tên và thêm RedirectAttributes
        logger.debug("Yêu cầu xem phản hồi cho questionId: {}", questionId);
        SurveyQuestion question = surveyQuestionService.getSurveyQuestionById(questionId);
        if (question == null) {
            logger.warn("Không tìm thấy câu hỏi với ID: {}", questionId);
            redirectAttributes.addFlashAttribute("errorMessage", "Câu hỏi không tồn tại (ID: " + questionId + ").");
          
            return "redirect:/surveys";
        }

        List<SurveyResponse> responses = surveyResponseService.getResponsesByQuestionId(questionId);
        model.addAttribute("responses", responses);
        model.addAttribute("question", question);
        logger.info("Hiển thị {} phản hồi cho câu hỏi '{}' (ID: {})", (responses != null ? responses.size() : 0), question.getQuestionText(), questionId);
        return "response_management";
    }

    // MỚI: Hiển thị tất cả các phản hồi cho một khảo sát
    @GetMapping("/survey/{surveyId}")
    public String listResponsesForSurvey(@PathVariable("surveyId") int surveyId, Model model, RedirectAttributes redirectAttributes) {
        logger.debug("Yêu cầu xem tất cả phản hồi cho surveyId: {}", surveyId);
        Survey survey = surveyService.getSurveyById(surveyId);

        if (survey == null) {
            logger.warn("Không tìm thấy khảo sát với ID: {}", surveyId);
            redirectAttributes.addFlashAttribute("errorMessage", "Khảo sát không tồn tại (ID: " + surveyId + ").");
            return "redirect:/surveys";
        }

        List<SurveyQuestion> questions = surveyQuestionService.getQuestionsBySurveyId(surveyId);
        if (questions == null) {
            questions = new ArrayList<>(); // Tránh NullPointerException
            logger.info("Khảo sát '{}' (ID: {}) không có câu hỏi nào.", survey.getTitle(), surveyId);
        }

        Map<SurveyQuestion, List<SurveyResponse>> responsesByQuestionMap = new LinkedHashMap<>();
        boolean hasAnyActualResponses = false;

        for (SurveyQuestion questionLoopVar : questions) { // Đổi tên biến lặp để tránh nhầm lẫn
            // Lấy thông tin chi tiết câu hỏi để đảm bảo các thuộc tính (như options) được tải
            SurveyQuestion detailedQuestion = surveyQuestionService.getSurveyQuestionById(questionLoopVar.getQuestionId());
            if (detailedQuestion != null) {
                List<SurveyResponse> responses = surveyResponseService.getResponsesByQuestionId(detailedQuestion.getQuestionId());
                responsesByQuestionMap.put(detailedQuestion, responses);
                if (responses != null && !responses.isEmpty()) {
                    hasAnyActualResponses = true;
                }
            } else {
                 logger.warn("Câu hỏi với ID: {} (thuộc survey ID: {}) không tìm thấy khi lấy chi tiết.", questionLoopVar.getQuestionId(), surveyId);
            }
        }

        model.addAttribute("survey", survey);
        model.addAttribute("responsesByQuestionMap", responsesByQuestionMap);
        model.addAttribute("hasActualResponses", hasAnyActualResponses);
        logger.info("Hiển thị tổng hợp phản hồi cho khảo sát '{}' (ID: {}). Có phản hồi thực tế: {}", survey.getTitle(), surveyId, hasAnyActualResponses);
        return "survey_all_responses"; 
    }

    // Thêm phản hồi
    @PostMapping("/submit")
    public String submitResponse(@ModelAttribute SurveyResponse response, RedirectAttributes redirectAttributes) {
        logger.debug("Nhận yêu cầu submit phản hồi.");
        if (response.getQuestionId() == null || response.getQuestionId().getQuestionId() == null) {
            logger.warn("Submit phản hồi thất bại: thiếu thông tin questionId.");
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể gửi phản hồi do thiếu thông tin câu hỏi.");
            return "redirect:/surveys"; 
        }
        
        Integer questionId = response.getQuestionId().getQuestionId();
        logger.debug("Phản hồi cho questionId: {}", questionId);
        SurveyQuestion question = surveyQuestionService.getSurveyQuestionById(questionId);

        if (question == null) {
            logger.warn("Submit phản hồi thất bại: câu hỏi với ID {} không tồn tại.", questionId);
            redirectAttributes.addFlashAttribute("errorMessage", "Câu hỏi không hợp lệ hoặc không tồn tại.");
           
            return "redirect:/surveys";
        }
      

        try {
            surveyResponseService.addSurveyResponse(response);
            logger.info("Phản hồi cho câu hỏi ID {} đã được lưu thành công.", questionId);
            redirectAttributes.addFlashAttribute("successMessage", "Phản hồi của bạn đã được ghi nhận thành công!");
        } catch (Exception e) {
            logger.error("Lỗi khi lưu phản hồi cho câu hỏi ID {}: {}", questionId, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi ghi nhận phản hồi của bạn: " + e.getMessage());
        }
        
       
        if (question.getSurveyId() != null && question.getSurveyId().getSurveyId() != null) { // Sử dụng getSurveyId() để lấy ID từ đối tượng Survey
             logger.debug("Redirecting to survey responses page for survey ID: {}", question.getSurveyId().getSurveyId());
             return "redirect:/responses/survey/" + question.getSurveyId().getSurveyId(); // Không cần ?success vì FlashAttribute sẽ tự hiển thị
        }
       
        logger.debug("Fallback: Redirecting to question responses page for question ID: {}", questionId);
        return "redirect:/responses/" + questionId;
    }
}
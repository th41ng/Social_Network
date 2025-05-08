/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;

import com.socialapp.pojo.SurveyQuestion;
import com.socialapp.pojo.SurveyResponse;
import com.socialapp.service.SurveyQuestionService;
import com.socialapp.service.SurveyResponseService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author DELL G15
 */
@Controller
@RequestMapping("/responses")
public class SurveyResponseController {

    @Autowired
    private SurveyResponseService surveyResponseService;

    @Autowired
    private SurveyQuestionService surveyQuestionService;

    // Hiển thị các phản hồi cho câu hỏi
    @GetMapping("/{questionId}")
    public String listResponses(@PathVariable("questionId") int questionId, Model model) {
        List<SurveyResponse> responses = surveyResponseService.getResponsesByQuestionId(questionId);
        SurveyQuestion question = surveyQuestionService.getSurveyQuestionById(questionId);
        model.addAttribute("responses", responses);
        model.addAttribute("question", question);
        return "response_management"; 
    }

    
    // Thêm phản hồi
    @PostMapping("/submit")
    public String submitResponse(@ModelAttribute SurveyResponse response) {
      
        if (response.getOptionId() == null) {
            response.setResponseText(response.getResponseText()); // Lưu phản hồi tự luận
        }
        surveyResponseService.addSurveyResponse(response);
        return "redirect:/responses/" + response.getQuestionId().getQuestionId();
    }

}

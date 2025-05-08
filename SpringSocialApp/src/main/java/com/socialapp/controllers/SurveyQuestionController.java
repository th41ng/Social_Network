/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;
import com.socialapp.pojo.Survey;
import com.socialapp.pojo.SurveyQuestion;
import com.socialapp.service.SurveyQuestionService;
import com.socialapp.service.SurveyService;
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
@RequestMapping("/questions")
public class SurveyQuestionController {

    @Autowired
    private SurveyQuestionService surveyQuestionService;

    @Autowired
    private SurveyService surveyService;

    // Hiển thị câu hỏi của một khảo sát
    @GetMapping("/{surveyId}")
    public String listQuestions(@PathVariable("surveyId") int surveyId, Model model) {
        Survey survey = surveyService.getSurveyById(surveyId);
        if (survey == null) {
           
            return "redirect:/surveys"; 
        }
        List<SurveyQuestion> questions = surveyQuestionService.getQuestionsBySurveyId(surveyId);
        model.addAttribute("survey", survey);
        model.addAttribute("questions", questions);
        return "question_management";
    }

    // Thêm câu hỏi vào khảo sát
    @GetMapping("/add/{surveyId}")
    public String addQuestionForm(@PathVariable("surveyId") int surveyId, Model model) {
        Survey survey = surveyService.getSurveyById(surveyId);
        if (survey == null) {
           
            return "redirect:/surveys"; 
        }
        SurveyQuestion newQuestion = new SurveyQuestion();
        
        model.addAttribute("survey", survey); // survey này dùng để lấy surveyId cho hidden input trong form
        model.addAttribute("question", newQuestion);
        return "question_form";
    }

    // Lưu câu hỏi
    @PostMapping("/save")
    public String saveQuestion(@ModelAttribute SurveyQuestion question, @RequestParam("surveyObjectId") int surveyObjectId, Model model) {
        Survey survey = surveyService.getSurveyById(surveyObjectId);
        if (survey == null) {
          
            model.addAttribute("errorMessage", "Khảo sát không hợp lệ hoặc không tồn tại.");
            model.addAttribute("question", question); 
            
            return "redirect:/surveys"; 
        }

        question.setSurveyId(survey); // Gán đối tượng Survey đã được tải vào SurveyQuestion

        try {
            surveyQuestionService.addSurveyQuestion(question);
        } catch (Exception e) {
            
            model.addAttribute("errorMessage", "Lỗi khi lưu câu hỏi: " + e.getMessage());
            model.addAttribute("survey", survey); 
            model.addAttribute("question", question);
            return "question_form";
        }
        
        
        return "redirect:/questions/" + question.getSurveyId().getSurveyId();
    }

    // Xóa câu hỏi
    @GetMapping("/delete/{questionId}")
    public String deleteQuestion(@PathVariable("questionId") int questionId, Model model) {
        SurveyQuestion question = surveyQuestionService.getSurveyQuestionById(questionId);
        if (question == null || question.getSurveyId() == null) {
           
             model.addAttribute("errorMessage", "Câu hỏi không hợp lệ hoặc không tìm thấy.");
           
            return "redirect:/surveys";
        }
        int surveyIdToGoBackTo = question.getSurveyId().getSurveyId(); // Lấy ID trước khi xóa
        surveyQuestionService.deleteSurveyQuestion(questionId);
        return "redirect:/questions/" + surveyIdToGoBackTo;
    }
}
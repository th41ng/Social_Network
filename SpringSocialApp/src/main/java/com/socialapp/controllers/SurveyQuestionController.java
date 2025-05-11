/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;

import com.socialapp.pojo.Survey;
import com.socialapp.pojo.SurveyQuestion;
import com.socialapp.pojo.QuestionType;
import com.socialapp.service.SurveyQuestionService;
import com.socialapp.service.SurveyService;
import com.socialapp.service.QuestionTypeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

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

    @Autowired
    private QuestionTypeService questionTypeService;

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
        return "question_management"; // Trang hiển thị danh sách câu hỏi
    }

    // Hiển thị form thêm câu hỏi
    @GetMapping("/add/{surveyId}")
    public String addQuestionForm(@PathVariable("surveyId") int surveyId, Model model) {
        Survey survey = surveyService.getSurveyById(surveyId);
        if (survey == null) {
            return "redirect:/surveys";
        }
        SurveyQuestion newQuestion = new SurveyQuestion();
        // newQuestion.setSurveyId(survey); // Có thể gán survey ở đây hoặc trong POST

        model.addAttribute("survey", survey);
        model.addAttribute("question", newQuestion);
        model.addAttribute("questionTypes", this.questionTypeService.getQuestionTypes());
        return "question_form";
    }

    // Lưu câu hỏi
    @PostMapping("/save")
    public String saveQuestion(@ModelAttribute("question") @Valid SurveyQuestion question,
            BindingResult result, // Để bắt lỗi validation nếu có
            @RequestParam("surveyObjectId") int surveyObjectId,
            @RequestParam("selectedTypeId") int selectedTypeId,
            Model model) {

        Survey survey = surveyService.getSurveyById(surveyObjectId);
        if (survey == null) {
            model.addAttribute("errorMessage", "Khảo sát không hợp lệ hoặc không tồn tại.");
            // Cần load lại questionTypes cho form nếu có lỗi
            model.addAttribute("questionTypes", this.questionTypeService.getQuestionTypes());
            model.addAttribute("survey", new Survey(surveyObjectId));
            return "question_form";
        }
        question.setSurveyId(survey);

        // **Lấy và gán QuestionType cho câu hỏi**
        QuestionType selectedQuestionType = this.questionTypeService.getQuestionTypeById(selectedTypeId);
        if (selectedQuestionType == null) {
            model.addAttribute("errorMessage", "Loại câu hỏi không hợp lệ.");
            model.addAttribute("questionTypes", this.questionTypeService.getQuestionTypes());
            model.addAttribute("survey", survey);
            return "question_form";
        }
        question.setTypeId(selectedQuestionType);

        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.");
            model.addAttribute("questionTypes", this.questionTypeService.getQuestionTypes());
            model.addAttribute("survey", survey);
            return "question_form";
        }

        try {
            surveyQuestionService.addSurveyQuestion(question);
        } catch (Exception e) {

            System.err.println("Lỗi khi lưu câu hỏi: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Lỗi khi lưu câu hỏi: " + e.getMessage());
            model.addAttribute("questionTypes", this.questionTypeService.getQuestionTypes());
            model.addAttribute("survey", survey);
            return "question_form";
        }

        // Chuyển hướng về trang quản lý câu hỏi của survey đó
        return "redirect:/questions/" + question.getSurveyId().getSurveyId();
    }

    // Xóa câu hỏi
    @GetMapping("/delete/{questionId}")
    public String deleteQuestion(@PathVariable("questionId") int questionId, Model model) {
        SurveyQuestion question = surveyQuestionService.getSurveyQuestionById(questionId);
        if (question == null || question.getSurveyId() == null) {

            return "redirect:/surveys";
        }
        int surveyIdToGoBackTo = question.getSurveyId().getSurveyId();
        surveyQuestionService.deleteSurveyQuestion(questionId);
        return "redirect:/questions/" + surveyIdToGoBackTo;
    }

    @GetMapping("/edit/{questionId}")
    public String editQuestionForm(@PathVariable("questionId") int questionId, Model model) {
        SurveyQuestion question = surveyQuestionService.getSurveyQuestionById(questionId);
        if (question == null || question.getSurveyId() == null) {
            return "redirect:/surveys";
        }
        model.addAttribute("question", question);
        model.addAttribute("survey", question.getSurveyId());
        model.addAttribute("questionTypes", this.questionTypeService.getQuestionTypes());
        return "question_form";
    }
}

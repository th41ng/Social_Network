/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;
import com.socialapp.pojo.SurveyQuestion;
import com.socialapp.pojo.SurveyOption;
import com.socialapp.service.SurveyOptionService;
import com.socialapp.service.SurveyQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
/**
 *
 * @author DELL G15
 */
@Controller
@RequestMapping("/options")
public class SurveyOptionController {

    @Autowired
    private SurveyOptionService surveyOptionService;

    @Autowired
    private SurveyQuestionService surveyQuestionService;

    @GetMapping("/add/{questionId}")
    public String addOptionForm(@PathVariable("questionId") int questionId, Model model) {
        SurveyQuestion question = surveyQuestionService.getSurveyQuestionById(questionId);
        model.addAttribute("question", question);
        model.addAttribute("option", new SurveyOption());
        return "option_form";
    }

    @PostMapping("/save")
    public String saveOption(@ModelAttribute SurveyOption option) {
        surveyOptionService.addSurveyOption(option);
        return "redirect:/questions/" + option.getQuestionId().getSurveyId().getSurveyId();
    }

    @GetMapping("/delete/{optionId}")
    public String deleteOption(@PathVariable("optionId") int optionId) {
        SurveyOption option = surveyOptionService.getSurveyOptionById(optionId);
        surveyOptionService.deleteSurveyOption(optionId);
        return "redirect:/questions/" + option.getQuestionId().getSurveyId().getSurveyId();
    }
}
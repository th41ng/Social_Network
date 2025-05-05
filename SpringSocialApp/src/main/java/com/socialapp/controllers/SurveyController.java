/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;

import com.socialapp.pojo.Survey;
import com.socialapp.service.CategoryService;
import com.socialapp.service.SurveyService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author DELL G15
 */
@Controller
@ControllerAdvice
@RequestMapping("/surveys")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private CategoryService categoryService;

    
    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("categories", this.categoryService.getCategories());
    }

    // Hiển thị danh sách khảo sát
    @GetMapping
    public String listSurveys(@RequestParam Map<String, String> params, Model model) {
        List<Survey> surveys = this.surveyService.getSurveys(params);

        model.addAttribute("surveys", surveys);
        model.addAttribute("params", params); // Giữ lại tham số tìm kiếm
        return "survey_management"; 
    }
}
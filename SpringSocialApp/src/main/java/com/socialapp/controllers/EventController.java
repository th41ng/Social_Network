/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;

import com.socialapp.pojo.EventNotification;
import com.socialapp.service.CategoryService;
import com.socialapp.service.EventNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 *
 * @author DELL G15
 */

@Controller
@ControllerAdvice
public class EventController {

    @Autowired
    private EventNotificationService eventNotificationService;

    @Autowired
    private CategoryService categoryService;

    /**
     * Thêm thuộc tính chung vào Model
     */
    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("categories", this.categoryService.getCategories());
    }

    @RequestMapping("/Events")
    public String listEvents(@RequestParam Map<String, String> params, Model model) {
        List<EventNotification> event_notification = this.eventNotificationService.getNotifications(params);
        model.addAttribute("events", event_notification);
        return "event_management"; // Trả về file Thymeleaf "events.html"
    }
}

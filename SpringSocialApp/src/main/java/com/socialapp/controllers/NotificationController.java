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
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author DELL G15
 */
//
@RequestMapping("/Notification")
@Controller
@ControllerAdvice
public class NotificationController {

    @Autowired
    private EventNotificationService eventNotificationService;

    @Autowired
    private CategoryService categoryService;

   
    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("categories", this.categoryService.getCategories());
    }

    public String listNotification(@RequestParam Map<String, String> params, Model model) {
        List<EventNotification> event_notification = this.eventNotificationService.getNotifications(params);
        System.out.println("Notifications fetched: " + event_notification.size()); 
        model.addAttribute("notification", event_notification);
        return "notification_management"; 
    }
}


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;

import com.socialapp.pojo.Event;
import com.socialapp.pojo.EventNotification;
import com.socialapp.pojo.User;
import com.socialapp.pojo.UserGroups;
import com.socialapp.service.CategoryService;
import com.socialapp.service.EventNotificationService;
import com.socialapp.service.EmailService;
import com.socialapp.service.EventService;
import com.socialapp.service.UserGroupService;
import com.socialapp.service.UserService;
import java.util.Date;
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
import org.springframework.web.bind.annotation.PostMapping;

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
    private EventService eventService;
    @Autowired
    private UserGroupService userGroupService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;

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

    @GetMapping("/add")
    public String showAddNotificationForm(@RequestParam Map<String, String> params, Model model) {
        // Lấy danh sách sự kiện từ database
        List<Event> events = eventService.getEvents(params);

        // Lấy danh sách nhóm từ database
        List<UserGroups> groups = userGroupService.getAllGroups(params);

        List<User> recievedUser = userService.getAllUsers(params);

        // Thêm vào model để truyền sang view
        model.addAttribute("notification", new EventNotification());
        model.addAttribute("events", events);
        model.addAttribute("groups", groups);
        model.addAttribute("recievedUser", recievedUser);

        return "add_update_notification";
    }

    @PostMapping("/add")
    public String processAddNotification(
            @ModelAttribute("notification") EventNotification notification,
            Model model) {
        try {
            // Thiết lập thời gian gửi
            notification.setSentAt(new Date());

            // Lưu thông báo thông qua service
            this.eventNotificationService.addOrUpdateNotification(notification);

            // Chuyển hướng về trang danh sách thông báo
            return "redirect:/Notification";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi thêm thông báo: " + e.getMessage());
            return "add_update_notification"; // Trả về form nếu có lỗi
        }
    }

}

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

    /**
     * Danh sách sự kiện
     */
    @RequestMapping("/Events")
    public String listEvents(@RequestParam Map<String, String> params, Model model) {
        List<EventNotification> event_notification = this.eventNotificationService.getNotifications(params);
        System.out.println("Notifications fetched: " + event_notification.size()); // Debugging
        model.addAttribute("eventNotifications", event_notification);
        return "event_management"; // Trả về file Thymeleaf "events.html"
    }

    /**
     * Thêm sự kiện mới
     */
    @PostMapping("/event-notifications/add")
    public String addEvent(
        @RequestParam("title") String title,
        @RequestParam("content") String content,
        @RequestParam("eventId") int eventId,
        @RequestParam(value = "receiverUserId", required = false) Integer receiverUserId,
        @RequestParam(value = "groupId", required = false) Integer groupId) {

    EventNotification eventNotification = new EventNotification();
    eventNotification.setTitle(title);
    eventNotification.setContent(content);
    eventNotification.setEventId(eventId);
    eventNotification.setReceiverUserId(receiverUserId);
    eventNotification.setGroupId(groupId);

    this.eventNotificationService.addOrUpdateNotification(eventNotification);
    return "redirect:/Events";
}

    /**
     * Xóa sự kiện
     */
   @PostMapping("/event-notifications/delete/{id}")
    public String deleteEvent(@PathVariable("id") int id) {
    eventNotificationService.deleteNotification(id);  // Xoá sự kiện theo ID
    return "redirect:/Events";  // Chuyển hướng lại trang quản lý sự kiện
}


}

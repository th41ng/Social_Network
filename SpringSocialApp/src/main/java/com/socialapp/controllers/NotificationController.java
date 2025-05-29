/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;

import com.socialapp.pojo.Event;
import com.socialapp.pojo.EventNotification;
import com.socialapp.pojo.User;
import com.socialapp.pojo.UserGroups;
import com.socialapp.repository.impl.EventNotificationRepositoryImpl;
import static com.socialapp.repository.impl.EventNotificationRepositoryImpl.PAGE_SIZE;
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

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("/listNotification")
    public String listNotification(@RequestParam Map<String, String> params, Model model) {
        String pageParam = params.get("page");
        int page = (pageParam == null || pageParam.trim().isEmpty()) ? 1 : Integer.parseInt(pageParam);
        if (page < 1) {
            page = 1;
        }
        // Đảm bảo params luôn có "page" cho repository và để giữ lại trên URL khi chuyển trang
        params.put("page", String.valueOf(page));

        List<EventNotification> event_notification = this.eventNotificationService.getNotifications(params);
        long totalNotis = this.eventNotificationService.countNotis();

        int pageSize = EventNotificationRepositoryImpl.PAGE_SIZE;
        int totalNotiPage = (int) Math.ceil((double) totalNotis / pageSize);

        model.addAttribute("params", params);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalNotiPage);
        model.addAttribute("notification", event_notification);
        return "notification_management";
    }

    @GetMapping("/add")
    public String showAddNotificationForm(@RequestParam Map<String, String> params, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = auth.getName();  // Lấy username hiện tại
        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            System.out.println("User không tồn tại!");
        } else {
            System.out.println("User id: " + currentUser.getId());
            System.out.println("User username: " + currentUser.getUsername());
        }
        int adminId = currentUser.getId();

        // Lấy danh sách sự kiện từ database
        List<Event> events = eventService.getAvailableEvents(params);

        // Lấy danh sách nhóm từ database
        List<UserGroups> groups = userGroupService.getAllGroups(params);

        List<User> recievedUser = userService.getAllUsers(params);

        // Thêm vào model để truyền sang view
        model.addAttribute("notification", new EventNotification());
        model.addAttribute("events", events);
        model.addAttribute("groups", groups);
        model.addAttribute("recievedUser", recievedUser);
        model.addAttribute("adminId", adminId); // Thêm adminId tự động

        return "add_update_notification";
    }

    @PostMapping("/add")
    public String processAddNotification(
            @ModelAttribute("notification") EventNotification notification,
            @RequestParam("adminId") Integer adminId,
            @RequestParam("eventId") Integer eventId,
            @RequestParam(value = "receiverUserId", required = false) Integer receiverUserId,
            @RequestParam(value = "groupId", required = false) Integer groupId,
            Model model) {
        try {
            // Lấy admin từ adminId
            User admin = userService.getUserById(adminId);
            if (admin == null) {
                throw new IllegalArgumentException("Admin không hợp lệ.");
            }
            notification.setAdmin(admin);

            // Lấy event từ eventId
            Event event = eventService.getEventById(eventId);
            if (event == null) {
                throw new IllegalArgumentException("Sự kiện không hợp lệ.");
            }
            notification.setEvent(event);

            // Xử lý receiverUserId
            if (receiverUserId != null) {
                User receiverUser = userService.getUserById(receiverUserId);
                if (receiverUser == null) {
                    throw new IllegalArgumentException("Người nhận không hợp lệ.");
                }
                notification.setReceiverUser(receiverUser);
            } else {
                notification.setReceiverUser(null); // Hoặc giữ nguyên giá trị mặc định
            }

            // Xử lý groupId
            if (groupId != null) {
                UserGroups group = userGroupService.getGroupById(groupId);
                if (group == null) {
                    throw new IllegalArgumentException("Nhóm không hợp lệ.");
                }
                notification.setGroup(group);
            } else {
                notification.setGroup(null); // Hoặc giữ nguyên giá trị mặc định
            }

            // Thiết lập thời gian gửi
            notification.setSentAt(new Date());

            // Lưu thông báo
            eventNotificationService.addOrUpdateNotification(notification);

            return "redirect:/?categoryId=3";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi thêm thông báo: " + e.getMessage());
            return "add_update_notification"; // Trả về form nếu lỗi
        }
    }

    @GetMapping("/editNotification/{id}")
    public String showEditNotificationForm(
            @PathVariable("id") Integer id,
            Model model) {
        // Lấy thông báo cần sửa
        EventNotification notification = this.eventNotificationService.getNotificationById(id);
        if (notification == null) {
            throw new IllegalArgumentException("Không tìm thấy thông báo với ID: " + id);
        }

        // Lấy danh sách sự kiện, nhóm, và người dùng
        List<Event> events = eventService.getAvailableEvents(null);
        List<UserGroups> groups = userGroupService.getAllGroups(null);
        List<User> recievedUser = userService.getAllUsers(null);

        // Thêm dữ liệu vào model
        model.addAttribute("notification", notification);
        model.addAttribute("events", events);
        model.addAttribute("groups", groups);
        model.addAttribute("recievedUser", recievedUser);
        model.addAttribute("adminId", notification.getAdmin().getId()); // Giữ admin hiện tại

        return "edit_notification";
    }

    @PostMapping("/editNotification/{id}")
    public String processEditNotification(
            @PathVariable("id") Integer id,
            @ModelAttribute("notification") EventNotification notification,
            @RequestParam("adminId") Integer adminId,
            @RequestParam("eventId") Integer eventId,
            @RequestParam(value = "receiverUserId", required = false) Integer receiverUserId,
            @RequestParam(value = "groupId", required = false) Integer groupId,
            Model model) {
        try {
            EventNotification existingNotification = eventNotificationService.getNotificationById(id);
            if (existingNotification == null) {
                throw new IllegalArgumentException("Không tìm thấy thông báo với ID: " + id);
            }

            // Cập nhật thông tin cơ bản
            existingNotification.setTitle(notification.getTitle());
            existingNotification.setContent(notification.getContent());

            // Cập nhật sự kiện
            Event event = eventService.getEventById(eventId);
            if (event == null) {
                throw new IllegalArgumentException("Sự kiện không hợp lệ.");
            }
            existingNotification.setEvent(event);

            // Cập nhật người nhận
            if (receiverUserId != null) {
                User receiverUser = userService.getUserById(receiverUserId);
                if (receiverUser == null) {
                    throw new IllegalArgumentException("Người nhận không hợp lệ.");
                }
                existingNotification.setReceiverUser(receiverUser);
            } else {
                existingNotification.setReceiverUser(null);
            }

            // Cập nhật nhóm
            if (groupId != null) {
                UserGroups group = userGroupService.getGroupById(groupId);
                if (group == null) {
                    throw new IllegalArgumentException("Nhóm không hợp lệ.");
                }
                existingNotification.setGroup(group);
            } else {
                existingNotification.setGroup(null);
            }

            // Cập nhật thời gian gửi
            existingNotification.setSentAt(new Date());

            // Lưu thông báo
            eventNotificationService.addOrUpdateNotification(existingNotification);

            return "redirect:/?categoryId=3";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi cập nhật thông báo: " + e.getMessage());
            return "edit_notification";
        }
    }
}

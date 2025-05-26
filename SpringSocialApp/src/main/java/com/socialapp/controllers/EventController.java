/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;

import com.socialapp.pojo.Event;
import com.socialapp.pojo.EventNotification;
import com.socialapp.pojo.User;
import com.socialapp.service.EventService;
import com.socialapp.service.UserService;
import jakarta.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Admin
 */
@RequestMapping("/Event")
@Controller
@ControllerAdvice
public class EventController {

    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @GetMapping("/listEvent")
    public String listEvent(@RequestParam Map<String, String> params, Model model) {
        List<Event> event = this.eventService.getEvents(params);
        model.addAttribute("event", event);
        model.addAttribute("params", params);
        return "event_management";
    }

    // Hiển thị form thêm sự kiện
    @GetMapping("/add")
    public String showAddEventForm(Model model) {

        // Lấy thông tin người dùng hiện tại từ SecurityContextHolder
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // Lấy username hiện tại
        User currentUser = userService.getUserByUsername(username);

        if (currentUser == null) {
            throw new IllegalArgumentException("Người dùng không hợp lệ.");
        }

        // Thêm thông tin cần thiết vào model
        model.addAttribute("event", new Event()); // Đối tượng mới để binding
        model.addAttribute("adminId", currentUser.getId()); // adminId tự động

        return "add_event"; // Tên file HTML hiển thị form
    }

    // Xử lý thêm sự kiện
    @PostMapping("/add")
    public String addEvent(
            @ModelAttribute("event") @Valid Event event,
            BindingResult result,
            @RequestParam("adminId") int adminId,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("error", "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.");
            return "add_event";
        }

        try {
            User admin = userService.getUserById(adminId);
            if (admin == null) {
                throw new IllegalArgumentException("Admin không tồn tại.");
            }

            // Gán thông tin cho sự kiện
            event.setAdmin(admin); // Gán đối tượng admin
            event.setAdmin_id(admin.getId()); // Gán ID cho admin_id

            event.setStart_date(new Date()); // Bạn có thể thay đổi logic gán ngày tùy thuộc vào form
            event.setEnd_date(new Date());   // Tương tự như start_date

            // Lưu sự kiện vào cơ sở dữ liệu
            eventService.addOrUpdateEvent(event);

            // Reset form
            model.addAttribute("event", new Event());
            model.addAttribute("adminId", adminId);

            return "redirect:/Event/listEvent";
        } catch (Exception e) {
            // Thêm lỗi vào model để hiển thị trên giao diện
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "add_event";
        }
    }

// Hiển thị form sửa sự kiện
    // Hiển thị form sửa sự kiện
    @GetMapping("/edit/{id}")
    public String showeditEventForm(Model model, @PathVariable("id") int event_id) {
        // Lấy sự kiện cần sửa
        Event event = this.eventService.getEventById(event_id);
        if (event == null) {
            throw new IllegalArgumentException("Không tìm thấy sự kiện với ID: " + event_id);
        }

        // Lấy thông tin người dùng hiện tại từ SecurityContextHolder
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.getUserByUsername(username);

        if (currentUser == null) {
            throw new IllegalArgumentException("Người dùng không hợp lệ.");
        }

        // Thêm thông tin cần thiết vào model
        model.addAttribute("event", event); // Gán sự kiện từ cơ sở dữ liệu
        model.addAttribute("adminId", currentUser.getId());

        return "edit_event"; // Tên file HTML hiển thị form
    }

    @PostMapping("/edit/{id}")
    public String editEvent(
            @ModelAttribute("event") @Valid Event event,
            BindingResult result,
            @RequestParam("adminId") int adminId,
            @PathVariable("id") int eventId,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("error", "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.");
            model.addAttribute("adminId", adminId);

            // Lấy lại sự kiện gốc để hiển thị giá trị cũ trong form
            Event existingEvent = eventService.getEventById(eventId);
            if (existingEvent != null) {
                model.addAttribute("event", existingEvent);
            }
            return "edit_event";
        }

        try {
            // Lấy sự kiện gốc từ cơ sở dữ liệu
            Event existingEvent = eventService.getEventById(eventId);
            if (existingEvent == null) {
                throw new IllegalArgumentException("Không tìm thấy sự kiện với ID: " + eventId);
            }

            // Lấy admin từ adminId
            User admin = userService.getUserById(adminId);
            if (admin == null) {
                throw new IllegalArgumentException("Admin không tồn tại.");
            }

            // Gán giá trị mới vào sự kiện gốc (giữ lại giá trị cũ nếu không thay đổi)
            existingEvent.setTitle(event.getTitle());
            existingEvent.setDescription(event.getDescription());
            existingEvent.setStart_date(event.getStart_date());
            existingEvent.setEnd_date(event.getEnd_date());
            existingEvent.setLocation(event.getLocation());
            existingEvent.setAdmin(admin);

            // Cập nhật sự kiện
            eventService.addOrUpdateEvent(existingEvent);

            return "redirect:/Event/listEvent";
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            model.addAttribute("adminId", adminId);
            model.addAttribute("event", event); // Trả lại giá trị form người dùng đã nhập
            return "edit_event";
        }
    }
}

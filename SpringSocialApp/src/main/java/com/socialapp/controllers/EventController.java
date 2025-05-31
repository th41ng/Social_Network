/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;

import com.socialapp.pojo.Event;
import com.socialapp.pojo.User;
import com.socialapp.repository.impl.EventRepositoryImpl;
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
        String pageParam = params.get("page");
        int page = (pageParam == null || pageParam.trim().isEmpty()) ? 1 : Integer.parseInt(pageParam);
        if (page < 1) {
            page = 1;
        }
        params.put("page", String.valueOf(page));

        List<Event> event = this.eventService.getEvents(params);
        long totalEvent = this.eventService.countEvent();

        int pageSize = EventRepositoryImpl.PAGE_SIZE;
        int totalEventPages = (int) Math.ceil((double) totalEvent / pageSize);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalEventPages);
        model.addAttribute("event", event);
        model.addAttribute("params", params);
        return "event_management";
    }

    @GetMapping("/add")
    public String showAddEventForm(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); 
        User currentUser = userService.getUserByUsername(username);

        if (currentUser == null) {
            throw new IllegalArgumentException("Người dùng không hợp lệ.");
        }

        model.addAttribute("event", new Event());
        model.addAttribute("adminId", currentUser.getId()); 

        return "add_event"; 
    }

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
  
            event.setAdmin(admin); 
            event.setAdmin_id(admin.getId()); 

            event.setStart_date(new Date()); 
            event.setEnd_date(new Date());   

            eventService.addOrUpdateEvent(event);

            model.addAttribute("event", new Event());
            model.addAttribute("adminId", adminId);

            return "redirect:/Event/listEvent";
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "add_event";
        }
    }

    @GetMapping("/edit/{id}")
    public String showeditEventForm(Model model, @PathVariable("id") int event_id) {
        Event event = this.eventService.getEventById(event_id);
        if (event == null) {
            throw new IllegalArgumentException("Không tìm thấy sự kiện với ID: " + event_id);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.getUserByUsername(username);

        if (currentUser == null) {
            throw new IllegalArgumentException("Người dùng không hợp lệ.");
        }

        model.addAttribute("event", event); 
        model.addAttribute("adminId", currentUser.getId());

        return "edit_event"; 
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

            Event existingEvent = eventService.getEventById(eventId);
            if (existingEvent != null) {
                model.addAttribute("event", existingEvent);
            }
            return "edit_event";
        }

        try {
            Event existingEvent = eventService.getEventById(eventId);
            if (existingEvent == null) {
                throw new IllegalArgumentException("Không tìm thấy sự kiện với ID: " + eventId);
            }

            User admin = userService.getUserById(adminId);
            if (admin == null) {
                throw new IllegalArgumentException("Admin không tồn tại.");
            }

            existingEvent.setTitle(event.getTitle());
            existingEvent.setDescription(event.getDescription());
            existingEvent.setStart_date(event.getStart_date());
            existingEvent.setEnd_date(event.getEnd_date());
            existingEvent.setLocation(event.getLocation());
            existingEvent.setAdmin(admin);

            eventService.addOrUpdateEvent(existingEvent);

            return "redirect:/Event/listEvent";
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            model.addAttribute("adminId", adminId);
            model.addAttribute("event", event); 
            return "edit_event";
        }
    }
}

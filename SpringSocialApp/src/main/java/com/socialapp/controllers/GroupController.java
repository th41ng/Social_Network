/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;

import com.socialapp.pojo.Event;
import com.socialapp.pojo.GroupMembers;
import com.socialapp.pojo.User;
import com.socialapp.pojo.UserGroups;
import com.socialapp.service.EventService;
import com.socialapp.service.GroupMemberService;
import com.socialapp.service.UserGroupService;
import com.socialapp.service.UserService;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Admin
 */
@RequestMapping("/Group")
@Controller
@ControllerAdvice
public class GroupController {

    @Autowired
    private UserGroupService groupService;
    @Autowired
    private GroupMemberService groupMemberService;
    @Autowired
    private UserService userService;

    @GetMapping("/listGroup")
    public String listEvent(@RequestParam Map<String, String> params, Model model) {
        List<UserGroups> group = this.groupService.getAllGroups(params);

        model.addAttribute("params", params);
        model.addAttribute("group", group);
        return "group_management";
    }

    @GetMapping("/listMember")
    public String listMember(@RequestParam Map<String, String> params,
            @RequestParam(name = "groupId") int groupId,
            Model model) {
        // Lấy danh sách thành viên của nhóm dựa trên groupId
        List<GroupMembers> members = this.groupMemberService.getMembersByGroupId(groupId);
        UserGroups group = this.groupService.getGroupById(groupId);
        model.addAttribute("params", params);
        model.addAttribute("members", members);
        model.addAttribute("group", group);
        return "member_management";
    }

    @GetMapping("/add")
    public String showAddGroupForm(Model model) {
        // Lấy thông tin người dùng hiện tại từ SecurityContextHolder
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // Lấy username hiện tại
        User currentUser = userService.getUserByUsername(username);

        if (currentUser == null) {
            throw new IllegalArgumentException("Người dùng không hợp lệ.");
        }

        // Thêm thông tin cần thiết vào model
        model.addAttribute("newGroup", new UserGroups()); // Đối tượng mới để binding
        model.addAttribute("adminId", currentUser.getId()); // adminId tự động

        return "add_group"; // Tên file HTML hiển thị form
    }

    @PostMapping("/add")
    public String addGroup(
            @ModelAttribute("newGroup") UserGroups group,
            BindingResult result,
            @RequestParam("adminId") int adminId,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("error", "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.");
            return "add_group";
        }

        try {
            User admin = userService.getUserById(adminId);
            if (admin == null) {
                throw new IllegalArgumentException("Admin không tồn tại.");
            }

            // Gán thông tin cho nhóm
            group.setAdmin(admin);
            group.setAdminId(admin.getId());
            group.setCreatedAt(new Date());

            // Lưu nhóm vào cơ sở dữ liệu
            groupService.addOrUpdateGroup(group);

            // Reset form
            model.addAttribute("newGroup", new UserGroups());
            model.addAttribute("adminId", adminId);

            return "redirect:/Group/listGroup";
        } catch (Exception e) {
            // Thêm lỗi vào model để hiển thị trên giao diện
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "add_group";
        }
        
        
    }
}

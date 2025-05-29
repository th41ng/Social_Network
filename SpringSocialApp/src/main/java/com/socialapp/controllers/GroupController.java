/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;

import com.socialapp.pojo.Event;
import com.socialapp.pojo.GroupMembers;
import com.socialapp.pojo.User;
import com.socialapp.pojo.UserGroups;
import com.socialapp.repository.impl.UserGroupRepositoryImpl;
import com.socialapp.service.EventService;
import com.socialapp.service.GroupMemberService;
import com.socialapp.service.UserGroupService;
import com.socialapp.service.UserService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.PathVariable;
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
        String pageParam = params.get("page");
        int page = (pageParam == null || pageParam.trim().isEmpty()) ? 1 : Integer.parseInt(pageParam);
        if (page < 1) {
            page = 1;
        }
        // Đảm bảo params luôn có "page" cho repository và để giữ lại trên URL khi chuyển trang
        params.put("page", String.valueOf(page));
        long totalGroup = this.groupService.countGroup();
        // Sử dụng PAGE_SIZE đã import hoặc định nghĩa ở trên
        int pageSize = UserGroupRepositoryImpl.PAGE_SIZE;
        int totalGroupPages = (int) Math.ceil((double) totalGroup / pageSize);
        List<UserGroups> group = this.groupService.getAllGroups(params);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalGroupPages);
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
    public String showAddGroupForm(@RequestParam Map<String, String> params, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.getUserByUsername(username);

        if (currentUser == null) {
            throw new IllegalArgumentException("Người dùng không hợp lệ.");
        }

        List<User> users = userService.getAllUsers(params); // Lấy danh sách người dùng
        model.addAttribute("newGroup", new UserGroups());
        model.addAttribute("adminId", currentUser.getId());
        model.addAttribute("users", users); // Gửi danh sách người dùng sang giao diện

        return "add_group";
    }

    @PostMapping("/add")
    public String addGroup(
            @ModelAttribute("newGroup") UserGroups group,
            BindingResult result,
            @RequestParam("adminId") int adminId,
            @RequestParam(value = "memberIds", required = false) List<Integer> memberIds,
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

            group.setAdmin(admin);
            group.setAdminId(admin.getId());
            group.setCreatedAt(new Date());

            // Xử lý danh sách thành viên
            if (memberIds != null && !memberIds.isEmpty()) {
                for (Integer userId : memberIds) {
                    User user = userService.getUserById(userId);
                    if (user != null) {
                        GroupMembers member = new GroupMembers();
                        member.setGroup(group);
                        member.setUser(user);
                        member.setJoinedAt(new Date());
                        group.getMembers().add(member);
                    }
                }
            }

            groupService.addOrUpdateGroup(group);

            model.addAttribute("newGroup", new UserGroups());
            model.addAttribute("adminId", adminId);
            return "redirect:/Group/listGroup";
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "add_group";
        }
    }

    // Hiển thị form sửa nhóm
    @GetMapping("/edit/{id}")
    public String showEditGroupForm(Model model, @PathVariable("id") int groupId) {
        // Lấy nhóm cần sửa
        UserGroups group = this.groupService.getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Không tìm thấy nhóm với ID: " + groupId);
        }

        // Lấy thông tin người dùng hiện tại từ SecurityContextHolder
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.getUserByUsername(username);

        if (currentUser == null) {
            throw new IllegalArgumentException("Người dùng không hợp lệ.");
        }

        // Thêm thông tin cần thiết vào model
        model.addAttribute("group", group);

        model.addAttribute("adminId", currentUser.getId());

        return "edit_group"; // Tên file HTML hiển thị form
    }

    @PostMapping("/edit/{id}")
    public String editGroup(
            @ModelAttribute("group") @Valid UserGroups group,
            BindingResult result,
            @RequestParam("adminId") int adminId,
            @PathVariable("id") int groupId,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("error", "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.");
            model.addAttribute("adminId", adminId);

            // Lấy lại nhóm gốc để hiển thị giá trị cũ trong form
            UserGroups existingGroup = groupService.getGroupById(groupId);
            if (existingGroup != null) {
                model.addAttribute("group", existingGroup);
            }
            return "edit_group";
        }

        try {
            // Lấy nhóm gốc từ cơ sở dữ liệu
            UserGroups existingGroup = groupService.getGroupById(groupId);
            if (existingGroup == null) {
                throw new IllegalArgumentException("Không tìm thấy nhóm với ID: " + groupId);
            }

            // Lấy admin từ adminId
            User admin = userService.getUserById(adminId);
            if (admin == null) {
                throw new IllegalArgumentException("Admin không tồn tại.");
            }

            // Gán giá trị mới vào nhóm gốc
            existingGroup.setGroupName(group.getGroupName());
            existingGroup.setAdmin(admin);

            // Cập nhật nhóm
            groupService.addOrUpdateGroup(existingGroup);

            return "redirect:/Group/listGroup";
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            model.addAttribute("adminId", adminId);
            model.addAttribute("group", group);
            return "edit_group";
        }
    }

}

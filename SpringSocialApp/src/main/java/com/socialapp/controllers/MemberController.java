package com.socialapp.controllers;

import com.socialapp.pojo.GroupMembers;
import com.socialapp.pojo.User;
import com.socialapp.pojo.UserGroups;
import com.socialapp.service.GroupMemberService;
import com.socialapp.service.UserService;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/Member")
public class MemberController {

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    private UserService userService;

    // Hiển thị form thêm thành viên
    @GetMapping("/add")
    public String showAddMemberForm(@RequestParam("groupId") int groupId,
            @RequestParam Map<String, String> params,
            Model model) {
        List<User> users = this.userService.getAllUsers(params);

        model.addAttribute("groupId", groupId); // Gửi groupId sang view
        model.addAttribute("users", users); // Gửi danh sách người dùng sang view
        model.addAttribute("newMember", new GroupMembers()); // Tạo đối tượng mới để binding
        return "add_member"; // File HTML tương ứng
    }

    @PostMapping("/add")
    public String addMember(@ModelAttribute("newMember") GroupMembers groupMember,
            @RequestParam("groupId") int groupId,
            @RequestParam("userId") int userId) {
        // Retrieve the group by ID
        UserGroups group = new UserGroups();
        group.setGroupId(groupId);
        groupMember.setGroup(group);

        // Retrieve the user by ID
        User user = userService.getUserById(userId); // Assuming you have this method in your UserService
        groupMember.setUser(user);

        groupMember.setJoinedAt(new Date()); // Set join date
        groupMemberService.addOrUpdateMember(groupMember); // Save the member

        return "redirect:/Group/viewGroup?groupId=" + groupId; // Redirect back to the group view
    }
}

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

//    // Hiển thị form thêm thành viên
//    @GetMapping("/add")
//    public String showAddMemberForm(@RequestParam("groupId") int groupId,
//            @RequestParam Map<String, String> params,
//            Model model) {
//        List<User> users = this.userService.getAllUsers(params);
//
//        model.addAttribute("groupId", groupId); // Gửi groupId sang view
//        model.addAttribute("users", users); // Gửi danh sách người dùng sang view
//        model.addAttribute("newMember", new GroupMembers()); // Tạo đối tượng mới để binding
//        return "add_member"; // File HTML tương ứng
//    }
    @GetMapping("/add")
    public String showAddMemberForm(@RequestParam("groupId") int groupId, Model model) {
        // Lấy danh sách người dùng chưa có trong nhóm
        List<User> availableUsers = userService.getAvailableUsersForGroup(groupId);

        // Thêm vào model
        model.addAttribute("groupId", groupId);
        model.addAttribute("users", availableUsers);
        model.addAttribute("newMember", new GroupMembers());

        return "add_member"; // Tên file HTML hiển thị form
    }

//    @PostMapping("/add")
//    public String addMember(@ModelAttribute("newMember") GroupMembers groupMember,
//            @RequestParam("groupId") int groupId,
//            @RequestParam("userId") int userId) {
//        // Retrieve the group by ID
//        UserGroups group = new UserGroups();
//        group.setGroupId(groupId);
//        groupMember.setGroup(group);
//
//        // Retrieve the user by ID
//        User user = userService.getUserById(userId); // Assuming you have this method in your UserService
//        groupMember.setUser(user);
//
//        groupMember.setJoinedAt(new Date()); // Set join date
//        groupMemberService.addOrUpdateMember(groupMember); // Save the member
//
//        return "redirect:/Group/viewGroup?groupId=" + groupId; // Redirect back to the group view
//    }
    @PostMapping("/add")
    public String addMembersToGroup(
            @ModelAttribute("newMember") GroupMembers groupMember,
            @RequestParam("groupId") int groupId,
            @RequestParam("userIds") List<Integer> userIds) {
        try {
            // Lấy thông tin nhóm
            UserGroups group = new UserGroups();
            group.setGroupId(groupId);

            // Lặp qua danh sách userIds và thêm từng thành viên vào nhóm
            for (int userId : userIds) {
                // Lấy thông tin user
                User user = userService.getUserById(userId);
                if (user != null) {
                    GroupMembers newMember = new GroupMembers();
                    newMember.setGroup(group);
                    newMember.setUser(user);
                    newMember.setJoinedAt(new Date());

                    // Lưu thông tin thành viên
                    groupMemberService.addOrUpdateMember(newMember);
                }
            }

            // Redirect về trang nhóm sau khi thêm thành công
            return "redirect:/Group/listMember?groupId=" + groupId;
        } catch (Exception e) {
            // Xử lý lỗi và ghi log nếu cần
            return "redirect:/Group/addMember?groupId=" + groupId + "&error=" + e.getMessage();
        }
    }
}

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

    @GetMapping("/add")
    public String showAddMemberForm(@RequestParam("groupId") int groupId, Model model) {
        List<User> availableUsers = userService.getAvailableUsersForGroup(groupId);

        model.addAttribute("groupId", groupId);
        model.addAttribute("users", availableUsers);
        model.addAttribute("newMember", new GroupMembers());

        return "add_member"; 
    }

    @PostMapping("/add")
    public String addMembersToGroup(
            @ModelAttribute("newMember") GroupMembers groupMember,
            @RequestParam("groupId") int groupId,
            @RequestParam("userIds") List<Integer> userIds) {
        try {
            UserGroups group = new UserGroups();
            group.setGroupId(groupId);
    
            for (int userId : userIds) {
                User user = userService.getUserById(userId);
                if (user != null) {
                    GroupMembers newMember = new GroupMembers();
                    newMember.setGroup(group);
                    newMember.setUser(user);
                    newMember.setJoinedAt(new Date());

                    groupMemberService.addOrUpdateMember(newMember);
                }
            }

            return "redirect:/Group/listMember?groupId=" + groupId;
        } catch (Exception e) {
            return "redirect:/Group/addMember?groupId=" + groupId + "&error=" + e.getMessage();
        }
    }
}

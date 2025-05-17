/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.repository;

import com.socialapp.pojo.GroupMembers;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Admin
 */
public interface GroupMembersRepository {

    List<GroupMembers> getAllMembers(Map<String, String> params);

    GroupMembers getMemberById(int memberId);

    GroupMembers getMemberByGroupAndUserId(int groupId, int userId);

    GroupMembers addOrUpdateMember(GroupMembers groupMember);

    void deleteMember(int memberId);
//
//    void deleteMemberByGroupAndUserId(int groupId, int userId);

    List<GroupMembers> getMembersByGroupId(int groupId);

}

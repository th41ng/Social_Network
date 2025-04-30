package com.socialapp.service.impl;

import com.socialapp.pojo.GroupMembers;
import com.socialapp.repository.GroupMembersRepository;
import com.socialapp.service.GroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GroupMemberServiceImpl implements GroupMemberService {

    @Autowired
    private GroupMembersRepository groupMembersRepository;

    @Override
    public List<GroupMembers> getAllMembers(Map<String, String> params) {
        return groupMembersRepository.getAllMembers(params);
    }

    @Override
    public GroupMembers getMemberById(int memberId) {
        return groupMembersRepository.getMemberById(memberId);
    }

    @Override
    public GroupMembers getMemberByGroupAndUserId(int groupId, int userId) {
        return groupMembersRepository.getMemberByGroupAndUserId(groupId, userId);
    }

    @Override
    public GroupMembers addOrUpdateMember(GroupMembers groupMember) {
        return groupMembersRepository.addOrUpdateMember(groupMember);
    }

    @Override
    public void deleteMember(int memberId) {
        groupMembersRepository.deleteMember(memberId);
    }

    @Override
    public void deleteMemberByGroupAndUserId(int groupId, int userId) {
        groupMembersRepository.deleteMemberByGroupAndUserId(groupId, userId);
    }
  
}

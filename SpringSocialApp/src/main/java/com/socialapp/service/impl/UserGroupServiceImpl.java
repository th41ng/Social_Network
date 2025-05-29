package com.socialapp.service.impl;

import com.socialapp.pojo.UserGroups;
import com.socialapp.repository.UserGroupsRepository;
import com.socialapp.service.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserGroupServiceImpl implements UserGroupService {

    @Autowired
    private UserGroupsRepository userGroupsRepository;

    @Override
    public List<UserGroups> getAllGroups(Map<String, String> params) {
        return userGroupsRepository.getAllGroups(params);
    }

    @Override
    public UserGroups getGroupById(int groupId) {
        return userGroupsRepository.getGroupById(groupId);
    }
   

    @Override
    public UserGroups addOrUpdateGroup(UserGroups group) {
        return userGroupsRepository.addOrUpdateGroup(group);
    }

    @Override
    public void deleteGroup(int groupId) {
        userGroupsRepository.deleteGroup(groupId);
    }
    
     @Override
    public long countGroup() {
        return this.userGroupsRepository.countGroup();
    }
}


package com.socialapp.service;

import com.socialapp.pojo.UserGroups;
import java.util.List;
import java.util.Map;


public interface UserGroupService {


    List<UserGroups> getAllGroups(Map<String, String> params);

    UserGroups getGroupById(int groupId);

    long countGroup();

    UserGroups addOrUpdateGroup(UserGroups group);

    void deleteGroup(int groupId);

}

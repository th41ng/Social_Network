
package com.socialapp.repository;

import com.socialapp.pojo.GroupMembers;
import java.util.List;
import java.util.Map;


public interface GroupMembersRepository {

    List<GroupMembers> getAllMembers(Map<String, String> params);

    GroupMembers getMemberById(int memberId);

    GroupMembers getMemberByGroupAndUserId(int groupId, int userId);

    GroupMembers addOrUpdateMember(GroupMembers groupMember);

    void deleteMember(int memberId);


    List<GroupMembers> getMembersByGroupId(int groupId);

}

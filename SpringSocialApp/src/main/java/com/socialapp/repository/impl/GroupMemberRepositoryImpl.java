package com.socialapp.repository.impl;

import com.socialapp.pojo.GroupMembers;
import com.socialapp.repository.GroupMembersRepository;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
@Transactional
public class GroupMemberRepositoryImpl implements GroupMembersRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<GroupMembers> getAllMembers(Map<String, String> params) {
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createNamedQuery("GroupMembers.findAll", GroupMembers.class);

        if (params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            query.setMaxResults(10); 
            query.setFirstResult((page - 1) * 10);
        }

        return query.getResultList();
    }

    @Override
    public GroupMembers getMemberById(int memberId) {
        return sessionFactory.getCurrentSession().get(GroupMembers.class, memberId);
    }

    @Override
    public GroupMembers getMemberByGroupAndUserId(int groupId, int userId) {
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createNamedQuery("GroupMembers.findByGroupAndUserId", GroupMembers.class);
        query.setParameter("groupId", groupId);
        query.setParameter("userId", userId);
        return (GroupMembers) query.getResultList().stream().findFirst().orElse(null);
    }

    @Override
    public GroupMembers addOrUpdateMember(GroupMembers groupMember) {
        sessionFactory.getCurrentSession().saveOrUpdate(groupMember);
        return groupMember;
    }

    @Override
    public void deleteMember(int memberId) {
        Session session = sessionFactory.getCurrentSession();
        GroupMembers member = session.get(GroupMembers.class, memberId);
        if (member != null) {
            session.delete(member);
        }
    }


    @Override
    public List<GroupMembers> getMembersByGroupId(int groupId) {
        Session session = this.sessionFactory.getCurrentSession();
        String hql = "FROM GroupMembers gm WHERE gm.group.groupId = :groupId";
        Query query = session.createQuery(hql, GroupMembers.class);
        query.setParameter("groupId", groupId);
        return query.getResultList();
    }

}



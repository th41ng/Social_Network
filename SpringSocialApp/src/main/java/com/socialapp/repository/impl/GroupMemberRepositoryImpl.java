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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Repository Implementation for GroupMembers
 */
@Repository
@Transactional
public class GroupMemberRepositoryImpl implements GroupMembersRepository {

    private static final int PAGE_SIZE = 10;
    private static final Logger logger = LoggerFactory.getLogger(GroupMemberRepositoryImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<GroupMembers> getAllMembers(Map<String, String> params) {
        Session session = this.sessionFactory.getCurrentSession();
        String hql = "FROM GroupMembers gm WHERE 1=1";

        if (params != null) {
            if (params.containsKey("groupId")) {
                hql += " AND gm.group.id = :groupId";
            }
            if (params.containsKey("userId")) {
                hql += " AND gm.user.id = :userId";
            }
        }

        Query query = session.createQuery(hql, GroupMembers.class);

        if (params != null) {
            if (params.containsKey("groupId")) {
                query.setParameter("groupId", Integer.parseInt(params.get("groupId")));
            }
            if (params.containsKey("userId")) {
                query.setParameter("userId", Integer.parseInt(params.get("userId")));
            }
        }

        // Pagination
        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            query.setMaxResults(PAGE_SIZE);
            query.setFirstResult((page - 1) * PAGE_SIZE);
        }

        return query.getResultList();
    }

    @Override
    public GroupMembers getMemberById(int memberId) {
        Session session = this.sessionFactory.getCurrentSession();
        return session.get(GroupMembers.class, memberId);
    }

    @Override
    public GroupMembers getMemberByGroupAndUserId(int groupId, int userId) {
        Session session = this.sessionFactory.getCurrentSession();
        String hql = "FROM GroupMembers gm WHERE gm.group.id = :groupId AND gm.user.id = :userId";
        Query query = session.createQuery(hql, GroupMembers.class);
        query.setParameter("groupId", groupId);
        query.setParameter("userId", userId);

        List<GroupMembers> results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    @Override
    public GroupMembers addOrUpdateMember(GroupMembers groupMember) {
        Session session = this.sessionFactory.getCurrentSession();
        try {
            session.saveOrUpdate(groupMember);
            return groupMember;
        } catch (Exception e) {
            logger.error("Error adding or updating member: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void deleteMember(int memberId) {
        Session session = this.sessionFactory.getCurrentSession();
        GroupMembers member = session.get(GroupMembers.class, memberId);
        if (member != null) {
            session.delete(member);
        }
    }

    @Override
    public void deleteMemberByGroupAndUserId(int groupId, int userId) {
        Session session = this.sessionFactory.getCurrentSession();
        String hql = "DELETE FROM GroupMembers gm WHERE gm.group.id = :groupId AND gm.user.id = :userId";
        Query query = session.createQuery(hql);
        query.setParameter("groupId", groupId);
        query.setParameter("userId", userId);
        query.executeUpdate();
    }
}

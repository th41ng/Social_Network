package com.socialapp.repository.impl;

import com.socialapp.pojo.UserGroups;
import com.socialapp.repository.UserGroupsRepository;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Repository Implementation for UserGroups
 *
 * @author DELL G15
 */
@Repository
@Transactional
public class UserGroupRepositoryImpl implements UserGroupsRepository {

    @Autowired
    private SessionFactory sessionFactory;

    public static final int PAGE_SIZE = 5;

    @Override
    public List<UserGroups> getAllGroups(Map<String, String> params) {
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createNamedQuery("UserGroups.findAll", UserGroups.class);

        if (params != null && params.containsKey("groupName")) {
            query = session.createNamedQuery("UserGroups.findByName", UserGroups.class);
            query.setParameter("groupName", "%" + params.get("groupName") + "%");
        }

        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            query.setMaxResults(PAGE_SIZE);
            query.setFirstResult((page - 1) * PAGE_SIZE);
        }

        return query.getResultList();
    }

    @Override
    public UserGroups getGroupById(int groupId) {
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createNamedQuery("UserGroups.findById", UserGroups.class);
        query.setParameter("groupId", groupId);
        return (UserGroups) query.getSingleResult();
    }

    @Override
    public UserGroups addOrUpdateGroup(UserGroups group) {
        Session session = this.sessionFactory.getCurrentSession();
        session.saveOrUpdate(group);
        return group;
    }

    @Override
    public void deleteGroup(int groupId) {
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createNamedQuery("UserGroups.deleteById");
        query.setParameter("groupId", groupId);
        query.executeUpdate();
    }

    @Override
    public long countGroup() {
        Session session = this.sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery("SELECT COUNT(g.groupId) FROM UserGroups g", Long.class);
        Long count = query.getSingleResult();
        return count != null ? count : 0;
    }
}

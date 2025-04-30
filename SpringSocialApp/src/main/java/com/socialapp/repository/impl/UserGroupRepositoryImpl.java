package com.socialapp.repository.impl;

import com.socialapp.pojo.UserGroups;
import com.socialapp.repository.UserGroupsRepository;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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

    private static final int PAGE_SIZE = 10;

    @Override
    public List<UserGroups> getAllGroups(Map<String, String> params) {
        Session session = this.sessionFactory.getCurrentSession();
        String hql = "FROM UserGroups g WHERE 1=1";

        if (params != null) {
            if (params.containsKey("name")) {
                hql += " AND g.name LIKE :name";
            }
        }

        Query query = session.createQuery(hql, UserGroups.class);

        if (params != null && params.containsKey("name")) {
            query.setParameter("name", "%" + params.get("name") + "%");
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
    public UserGroups getGroupById(int groupId) {
        Session session = this.sessionFactory.getCurrentSession();
        return session.get(UserGroups.class, groupId);
    }

    @Override
    public UserGroups addOrUpdateGroup(UserGroups group) {
        Session session = this.sessionFactory.getCurrentSession();
        try {
            session.saveOrUpdate(group); // Thêm mới hoặc cập nhật
            return group;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteGroup(int groupId) {
        Session session = this.sessionFactory.getCurrentSession();
        UserGroups group = session.get(UserGroups.class, groupId);
        if (group != null) {
            session.delete(group); // Xóa cứng
        }
    }
}

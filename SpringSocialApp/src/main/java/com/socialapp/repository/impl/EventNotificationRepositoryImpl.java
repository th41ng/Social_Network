package com.socialapp.repository.impl;

import com.socialapp.pojo.EventNotification;
import com.socialapp.pojo.GroupMembers;
import com.socialapp.repository.EventNotificationRepository;
import org.hibernate.query.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class EventNotificationRepositoryImpl implements EventNotificationRepository {

    public static final int PAGE_SIZE = 5;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<EventNotification> getNotifications(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        Query<EventNotification> query = session.getNamedQuery("Notis.findAll");

        if (params != null && !params.isEmpty()) {
            StringBuilder hql = new StringBuilder("SELECT n FROM EventNotification n WHERE 1=1");
            if (params.containsKey("title")) {
                hql.append(" AND n.title LIKE :title");
            }
            query = session.createQuery(hql.toString(), EventNotification.class);
            if (params.containsKey("title")) {
                query.setParameter("title", "%" + params.get("title") + "%");
            }
        }
// Phân trang
        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            query.setFirstResult((page - 1) * PAGE_SIZE);
            query.setMaxResults(PAGE_SIZE);
        }
        return query.getResultList();
    }

    @Override
    public EventNotification getNotificationById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(EventNotification.class, id);
    }

    @Override
    public EventNotification addOrUpdateNotification(EventNotification notification) {
        Session session = this.factory.getObject().getCurrentSession();
        if (notification.getNotificationId() == null) {
            session.persist(notification);  // Insert đối tượng mới
        } else {
            session.merge(notification);  // Update đối tượng hiện tại
        }
        return notification;
    }

    @Override
    public void deleteNotification(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        EventNotification notification = this.getNotificationById(id);
        if (notification != null) {
            // Soft delete by setting the isDeleted flag to true
            //notification.setIsDeleted(true);
            //s.merge(notification);
            //Xóa cứng
            s.delete(notification);  // Xóa cứng bản ghi

        }
    }

    @Override
    public List<EventNotification> getNotificationsForUser(int userId, Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        Query<EventNotification> query = session.getNamedQuery("Notis.findForUser");

        // Get the user's group IDs
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Integer> groupQuery = builder.createQuery(Integer.class);
        Root<GroupMembers> groupRoot = groupQuery.from(GroupMembers.class);
        groupQuery.select(groupRoot.get("groupId"))
                .where(builder.equal(groupRoot.get("userId"), userId));
        List<Integer> userGroupIds = session.createQuery(groupQuery).getResultList();

        // Set query parameters
        query.setParameter("userId", userId);
        query.setParameter("groupIds", userGroupIds.isEmpty() ? List.of(-1) : userGroupIds); 
       

        // Apply pagination if needed
        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            query.setFirstResult((page - 1) * PAGE_SIZE);
            query.setMaxResults(PAGE_SIZE);
        }

        return query.getResultList();
    }

    @Override
    public long countNotis() {
        Session session = this.factory.getObject().getCurrentSession();
        Query<Long> query = session.createQuery("SELECT COUNT(n.notificationId) FROM EventNotification n", Long.class);
        Long count = query.getSingleResult();
        return count != null ? count : 0;
    }

}

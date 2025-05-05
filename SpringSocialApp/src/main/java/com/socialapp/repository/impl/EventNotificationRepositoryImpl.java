package com.socialapp.repository.impl;

import com.socialapp.pojo.EventNotification;
import com.socialapp.repository.EventNotificationRepository;
import jakarta.persistence.Query;
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

    private static final int PAGE_SIZE = 10;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<EventNotification> getNotifications(Map<String, String> params) {
    Session s = this.factory.getObject().getCurrentSession();
    CriteriaBuilder b = s.getCriteriaBuilder();
    CriteriaQuery<EventNotification> q = b.createQuery(EventNotification.class);
    Root<EventNotification> root = q.from(EventNotification.class);
    q.select(root);

    if (params != null) {
        List<Predicate> predicates = new ArrayList<>();

        // Filter by adminId
        String adminId = params.get("adminId");
        if (adminId != null && !adminId.isEmpty()) {
            predicates.add(b.equal(root.get("adminId"), Integer.parseInt(adminId)));
        }

        // Filter by eventId
        String eventId = params.get("eventId");
        if (eventId != null && !eventId.isEmpty()) {
            predicates.add(b.equal(root.get("eventId"), Integer.parseInt(eventId)));
        }

        // Filter by receiverUserId
        String receiverUserId = params.get("receiverUserId");
        if (receiverUserId != null && !receiverUserId.isEmpty()) {
            predicates.add(b.equal(root.get("receiverUserId"), Integer.parseInt(receiverUserId)));
        }

        // Filter by groupId
        String groupId = params.get("groupId");
        if (groupId != null && !groupId.isEmpty()) {
            predicates.add(b.equal(root.get("groupId"), Integer.parseInt(groupId)));
        }

        // Ensure isDeleted is false (soft delete filter)
        //predicates.add(b.equal(root.get("isDeleted"), false));
        
        q.where(predicates.toArray(Predicate[]::new));
    }

    Query query = s.createQuery(q);

    // Pagination
    if (params != null && params.containsKey("page")) {
        int page = Integer.parseInt(params.get("page"));
        query.setMaxResults(PAGE_SIZE);
        query.setFirstResult((page - 1) * PAGE_SIZE);
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
        Session s = this.factory.getObject().getCurrentSession();
        if (notification.getNotificationId() == null) {
            s.persist(notification);  // Insert new notification
        } else {
            s.merge(notification);  // Update existing notification
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
}

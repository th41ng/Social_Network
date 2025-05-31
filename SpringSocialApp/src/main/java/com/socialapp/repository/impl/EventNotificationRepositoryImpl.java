package com.socialapp.repository.impl;

import com.socialapp.controllers.ApiReactionController;
import com.socialapp.pojo.EventNotification;
import com.socialapp.pojo.GroupMembers;
import com.socialapp.pojo.User;
import com.socialapp.repository.EventNotificationRepository;
import com.socialapp.service.EmailService;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
@Transactional
public class EventNotificationRepositoryImpl implements EventNotificationRepository {

    private static final Logger logger = LoggerFactory.getLogger(ApiReactionController.class);
    public static final int PAGE_SIZE = 5;
    @Autowired
    private EmailService emailService;
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
            session.persist(notification);
        } else {
            session.merge(notification);
        }

        try {

            if (notification.getReceiverUser() != null) {
                String recipientEmail = notification.getReceiverUser().getEmail();
                emailService.sendNotiEmailtoUser(
                        recipientEmail,
                        "Thông báo sự kiện",
                        String.format(
                                "Xin chào %s,\n\n"
                                + "Bạn có một thông báo mới về sự kiện: %s.\n\n"
                                + "Vui lòng đăng nhập vào hệ thống để xem chi tiết.\n\n"
                                + "Trân trọng,\nĐội ngũ hỗ trợ.",
                                notification.getReceiverUser().getFullName(),
                                notification.getTitle()
                        )
                );
            }

            if (notification.getGroup() != null && notification.getGroup().getGroupId() != null) {
                List<String> groupEmails = getEmailsByGroupId(notification.getGroup().getGroupId());
                for (String email : groupEmails) {
                    emailService.sendNotiEmailtoUser(
                            email,
                            "Thông báo sự kiện",
                            String.format(
                                    "Xin chào,\n\n"
                                    + "Bạn có một thông báo mới về sự kiện: %s.\n\n"
                                    + "Vui lòng đăng nhập vào hệ thống để xem chi tiết.\n\n"
                                    + "Trân trọng,\nĐội ngũ hỗ trợ.",
                                    notification.getTitle()
                            )
                    );
                }
            }
        } catch (Exception e) {

            logger.error("Gửi thông báo qua email thất bại.", e);
        }

        return notification;
    }

    private List<String> getEmailsByGroupId(Integer groupId) {
        try {
            Session session = this.factory.getObject().getCurrentSession();
            return session.createQuery(
                    "SELECT u.email FROM User u JOIN GroupMembers gm ON u.id = gm.user.id "
                    + "WHERE gm.group.groupId = :groupId", String.class
            ).setParameter("groupId", groupId).getResultList();
        } catch (Exception e) {
            logger.error("Không thể lấy danh sách email cho groupId: " + groupId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public void deleteNotification(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        EventNotification notification = this.getNotificationById(id);
        if (notification != null) {

            s.delete(notification);

        }
    }

    @Override
    public List<EventNotification> getNotificationsForUser(int userId, Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        Query<EventNotification> query = session.getNamedQuery("Notis.findForUser");

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Integer> groupQuery = builder.createQuery(Integer.class);
        Root<GroupMembers> groupRoot = groupQuery.from(GroupMembers.class);
        groupQuery.select(groupRoot.get("groupId"))
                .where(builder.equal(groupRoot.get("userId"), userId));
        List<Integer> userGroupIds = session.createQuery(groupQuery).getResultList();

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

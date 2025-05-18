package com.socialapp.repository.impl;

import com.socialapp.pojo.Post;
import com.socialapp.pojo.User;
import com.socialapp.repository.UserRepository;
import com.socialapp.service.EmailService;
import jakarta.persistence.NoResultException;
import java.util.List;
import java.util.Map;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    private Session getCurrentSession() {
        return this.factory.getObject().getCurrentSession();
    }

    @Override
    public User getUserByUsername(String username) {
        Session s = getCurrentSession();
        try {
            Query<User> q = s.createNamedQuery("User.findByUsername", User.class);
            q.setParameter("username", username);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        Session s = getCurrentSession();
        try {
            Query<User> q = s.createNamedQuery("User.findByEmail", User.class);
            q.setParameter("email", email);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public User getUserById(int id) {
        return getCurrentSession().get(User.class, id);
    }

    @Override
    public User updateUser(User user) {
        Session s = getCurrentSession();
        s.update(user);
        return user;
    }

 @Override
    public  boolean deleteUser(int id) { // THAY ĐỔI: từ void thành boolean
        Session s = getCurrentSession();
        User user = s.get(User.class, id);
        if (user != null) {
            s.remove(user); // Hoặc s.delete(user) tùy phiên bản Hibernate/JPA
            return true; // Trả về true nếu tìm thấy và xóa
        }
        return false; // Trả về false nếu không tìm thấy user
    }

    @Override
    public User register(User u) {
        Session s = getCurrentSession();
        s.persist(u);
        s.refresh(u);
        return u;
    }

    @Override
    public boolean authenticate(String username, String password) {
        User u = this.getUserByUsername(username);
        if (u == null) return false;
        return this.passwordEncoder.matches(password, u.getPassword());
    }

    @Override
    public List<User> getAllUsers(Map<String, String> params) {
        Session session = getCurrentSession();
        Query<User> query = session.getNamedQuery("User.findAll");

        if (params != null && !params.isEmpty()) {
            StringBuilder hql = new StringBuilder("SELECT u FROM User u WHERE 1=1");
            if (params.containsKey("username")) {
                hql.append(" AND u.username LIKE :username");
            }
            if (params.containsKey("email")) {
                hql.append(" AND u.email LIKE :email");
            }
            query = session.createQuery(hql.toString(), User.class);
            if (params.containsKey("username")) {
                query.setParameter("username", "%" + params.get("username") + "%");
            }
            if (params.containsKey("email")) {
                query.setParameter("email", "%" + params.get("email") + "%");
            }
        }

        return query.getResultList();
    }


    @Override
    public long countUsers() {
        Session session = getCurrentSession();
        Query<Long> query = session.createQuery("SELECT COUNT(u.id) FROM User u", Long.class);
        Long count = query.getSingleResult();
        return count != null ? count : 0;
    }

    @Override
    public int countUsersRegisteredToday() {
        Session session = getCurrentSession();

        // HQL sử dụng hàm DATE() hoặc có thể thay đổi tùy DB (MySQL, PostgreSQL...) 
        // Đảm bảo trường createdAt kiểu java.util.Date hoặc java.sql.Timestamp
        Query<Long> query = session.createQuery(
            "SELECT COUNT(u.id) FROM User u WHERE DATE(u.createdAt) = CURRENT_DATE", Long.class);

        Long count = query.getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    @Override
    public void verifyStudent(int userId) {
        Session s = this.factory.getObject().getCurrentSession();
        User user = this.getUserById(userId);
        if (user != null) {
            user.setIsVerified(true);
            s.merge(user);
            // Gửi email thông báo xác nhận
            emailService.sendEmailtoStudent(
                    user.getEmail(),
                    "Tài khoản của bạn đã được xác nhận",
                    "Xin chào " + user.getFullName() + ",\n\n"
                    + "Tài khoản của bạn đã được xác nhận thành công. Bây giờ bạn có thể truy cập vào hệ thống.\n\n"
                    + "Trân trọng,\nĐội ngũ hỗ trợ."
            );
        }
    }
}

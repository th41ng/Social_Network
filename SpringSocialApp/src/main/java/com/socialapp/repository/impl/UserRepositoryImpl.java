package com.socialapp.repository.impl;

import com.socialapp.pojo.User;
import com.socialapp.repository.UserRepository;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import java.util.List;
import java.util.Map;
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

    @Override
    public User getUserByUsername(String username) {
        Session s = this.factory.getObject().getCurrentSession();
        try {
            Query q = s.createNamedQuery("User.findByUsername", User.class);
            q.setParameter("username", username);
            return (User) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        Session s = this.factory.getObject().getCurrentSession();
        try {
            Query q = s.createNamedQuery("User.findByEmail", User.class);
            q.setParameter("email", email);
            return (User) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public User getUserById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(User.class, id);
    }

    @Override
    public User updateUser(User user) {
        Session s = this.factory.getObject().getCurrentSession();
        s.update(user);
        return user;
    }

    @Override
    public void deleteUser(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        User user = s.get(User.class, id);
        if (user != null) {
            s.delete(user);
        }
    }

    @Override
    public User register(User u) {
        Session s = this.factory.getObject().getCurrentSession();
        s.persist(u);

        s.refresh(u);
        return u;
    }

    @Override
    public boolean authenticate(String username, String password) {
        User u = this.getUserByUsername(username);

        return this.passwordEncoder.matches(password, u.getPassword());
    }

    @Override
    public List<User> getAllUsers(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        Query query = session.getNamedQuery("User.findAll");  // Sử dụng NamedQuery để lấy tất cả người dùng

        // Nếu có các tham số lọc, bạn có thể thêm điều kiện vào query
        if (params != null && !params.isEmpty()) {
            StringBuilder queryBuilder = new StringBuilder("SELECT u FROM User u WHERE 1=1");

            params.forEach((key, value) -> {
                if (key.equals("username")) {
                    queryBuilder.append(" AND u.username LIKE :username");
                } else if (key.equals("email")) {
                    queryBuilder.append(" AND u.email LIKE :email");
                }
            });

            // Tạo lại query dựa trên các tham số lọc
            query = session.createQuery(queryBuilder.toString(), User.class);
            if (params.containsKey("username")) {
                query.setParameter("username", "%" + params.get("username") + "%");
            }
            if (params.containsKey("email")) {
                query.setParameter("email", "%" + params.get("email") + "%");
            }
        }

        return query.getResultList();  // Trả về kết quả danh sách người dùng
    }

    @Override
    public boolean verifyStudent(int userId) {
        Session session = this.factory.getObject().getCurrentSession();
        User user = session.get(User.class, userId);
        if (user != null) {
            user.setIsVerified(true);
            session.update(user);
            return true;
        }
        return false;
    }

}

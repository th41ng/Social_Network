package com.socialapp.repository.impl;

import com.socialapp.pojo.Users;
import com.socialapp.repository.UserRepository;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public Users getUserByUsername(String username) {
        Session s = this.factory.getObject().getCurrentSession();
        try {
            Query q = s.createNamedQuery("User.findByUsername", Users.class);
            q.setParameter("username", username);
            return (Users) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Users getUserByEmail(String email) {
        Session s = this.factory.getObject().getCurrentSession();
        try {
            Query q = s.createNamedQuery("User.findByEmail", Users.class);
            q.setParameter("email", email);
            return (Users) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Users getUserById(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Users.class, id);
    }

    @Override
    public Users addUser(Users user) {
        Session s = this.factory.getObject().getCurrentSession();
        s.persist(user);
        return user;
    }

    @Override
    public boolean authenticate(String usernameOrEmail, String password) {
        Session s = this.factory.getObject().getCurrentSession();
        try {
            String hql = "FROM Users u WHERE (u.username = :usernameOrEmail OR u.email = :usernameOrEmail) AND u.password = :password";
            Query q = s.createQuery(hql, Users.class);
            q.setParameter("usernameOrEmail", usernameOrEmail);
            q.setParameter("password", password);
            return !q.getResultList().isEmpty();
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public Users updateUser(Users user) {
        Session s = this.factory.getObject().getCurrentSession();
        s.update(user);
        return user;
    }

    @Override
    public void deleteUser(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        Users user = s.get(Users.class, id);
        if (user != null) {
            s.delete(user);
        }
    }
}

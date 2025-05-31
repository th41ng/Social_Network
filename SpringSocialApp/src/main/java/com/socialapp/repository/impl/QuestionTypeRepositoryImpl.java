/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.repository.impl;

import com.socialapp.pojo.QuestionType;
import com.socialapp.repository.QuestionTypeRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import jakarta.persistence.TypedQuery;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author DELL G15
 */
@Repository
@Transactional
public class QuestionTypeRepositoryImpl implements QuestionTypeRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<QuestionType> findAll() {
        Session session = this.factory.getObject().getCurrentSession();

        TypedQuery<QuestionType> query = session.createNamedQuery("QuestionType.findAll", QuestionType.class);
        return query.getResultList();
    }

    @Override
    public QuestionType findById(int typeId) {
        Session session = this.factory.getObject().getCurrentSession();

        return session.get(QuestionType.class, typeId);

    }
}

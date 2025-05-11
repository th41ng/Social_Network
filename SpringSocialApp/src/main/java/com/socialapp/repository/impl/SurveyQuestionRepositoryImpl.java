/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.repository.impl;

import com.socialapp.pojo.SurveyQuestion;
import com.socialapp.repository.SurveyQuestionRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author DELL G15
 */
@Repository
@Transactional
public class SurveyQuestionRepositoryImpl implements SurveyQuestionRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<SurveyQuestion> getQuestionsBySurveyId(int surveyId) {
        Session session = factory.getObject().getCurrentSession();
        // Truy vấn câu hỏi theo surveyId VÀ SẮP XẾP THEO questionOrder, sau đó là questionId
        String hql = "FROM SurveyQuestion sq WHERE sq.surveyId.surveyId = :surveyId ORDER BY sq.questionOrder ASC, sq.questionId ASC";
        return session.createQuery(hql, SurveyQuestion.class)
                .setParameter("surveyId", surveyId)
                .getResultList();
    }


    @Override
    public SurveyQuestion addSurveyQuestion(SurveyQuestion question) {
        Session session = factory.getObject().getCurrentSession();
        session.persist(question);
        return question;
    }

    @Override
    public void deleteSurveyQuestion(int questionId) {
        Session session = factory.getObject().getCurrentSession();
        SurveyQuestion question = session.get(SurveyQuestion.class, questionId);
        if (question != null) {
            session.remove(question);
        } else {
            throw new IllegalArgumentException("SurveyQuestion not found with ID: " + questionId);
        }
    }

    @Override
    public SurveyQuestion getSurveyQuestionById(int questionId) {
        Session session = factory.getObject().getCurrentSession();
        return session.get(SurveyQuestion.class, questionId);
    }
}

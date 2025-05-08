/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.repository.impl;

import com.socialapp.pojo.SurveyResponse;
import com.socialapp.repository.SurveyResponseRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author DELL G15
 */
@Repository
@Transactional
public class SurveyResponseRepositoryImpl implements SurveyResponseRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<SurveyResponse> getResponsesBySurveyId(int surveyId) {
        Session session = factory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<SurveyResponse> query = builder.createQuery(SurveyResponse.class);
        Root<SurveyResponse> root = query.from(SurveyResponse.class);
        query.select(root);

        Predicate surveyPredicate = builder.equal(root.get("surveyId").get("surveyId"), surveyId);
        query.where(surveyPredicate);

        return session.createQuery(query).getResultList();
    }

    @Override
    public SurveyResponse addSurveyResponse(SurveyResponse response) {
        Session session = factory.getObject().getCurrentSession();
        session.persist(response);
        return response;
    }

    @Override
    public void deleteSurveyResponse(int responseId) {
        Session session = factory.getObject().getCurrentSession();
        SurveyResponse response = session.get(SurveyResponse.class, responseId);
        if (response != null) {
            session.remove(response);
        } else {
            throw new IllegalArgumentException("SurveyResponse not found with ID: " + responseId);
        }
    }

    @Override
    public List<SurveyResponse> getResponsesByQuestionId(int questionId) {
        Session session = factory.getObject().getCurrentSession();
        // Truy vấn phản hồi theo questionId
        return session.createQuery("FROM SurveyResponse sr WHERE sr.questionId.questionId = :questionId", SurveyResponse.class)
                .setParameter("questionId", questionId) 
                .getResultList();
    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.repository.impl;

import com.socialapp.pojo.SurveyResponse;
import com.socialapp.repository.SurveyResponseRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.TypedQuery;

import java.util.List;

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
        jakarta.persistence.criteria.CriteriaBuilder builder = session.getCriteriaBuilder();
        jakarta.persistence.criteria.CriteriaQuery<SurveyResponse> query = builder.createQuery(SurveyResponse.class);
        jakarta.persistence.criteria.Root<SurveyResponse> root = query.from(SurveyResponse.class);
        query.select(root);

        jakarta.persistence.criteria.Predicate surveyPredicate = builder.equal(root.get("surveyId").get("surveyId"), surveyId);
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

            System.err.println("SurveyResponse not found with ID: " + responseId + " for deletion.");

        }
    }

    @Override
    public List<SurveyResponse> getResponsesByQuestionId(int questionId) {
        Session session = this.factory.getObject().getCurrentSession();

        String hql = "SELECT DISTINCT sr FROM SurveyResponse sr "
                + "LEFT JOIN FETCH sr.questionId q "
                + "LEFT JOIN FETCH q.surveyOptions "
                + "WHERE q.questionId = :questionId";

        TypedQuery<SurveyResponse> query = session.createQuery(hql, SurveyResponse.class);
        query.setParameter("questionId", questionId);
        return query.getResultList();
    }

    @Override
    public List<SurveyResponse> getResponsesBySurveyIdAndUserId(int surveyId, int userId) {
        Session session = this.factory.getObject().getCurrentSession();

        String hql = "FROM SurveyResponse sr WHERE sr.surveyId.surveyId = :surveyIdParam AND sr.userId.id = :userIdParam";
        TypedQuery<SurveyResponse> query = session.createQuery(hql, SurveyResponse.class);
        query.setParameter("surveyIdParam", surveyId);
        query.setParameter("userIdParam", userId);
        return query.getResultList();
    }

}

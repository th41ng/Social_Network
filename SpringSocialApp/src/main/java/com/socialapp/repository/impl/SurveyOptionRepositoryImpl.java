/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.repository.impl;
import com.socialapp.pojo.SurveyOption;
import com.socialapp.repository.SurveyOptionRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
/**
 *
 * @author DELL G15
 */
@Repository
@Transactional
public class SurveyOptionRepositoryImpl implements SurveyOptionRepository {

   @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public SurveyOption addSurveyOption(SurveyOption option) {
        Session session = factory.getObject().getCurrentSession();
        session.persist(option);
        return option;
    }

    @Override
    public void deleteSurveyOption(int optionId) {
        Session session = factory.getObject().getCurrentSession();
        SurveyOption option = session.get(SurveyOption.class, optionId);
        if (option != null) {
            session.remove(option);
        } else {
            throw new IllegalArgumentException("SurveyOption not found with ID: " + optionId);
        }
    }

    @Override
    public SurveyOption getSurveyOptionById(int optionId) {
        Session session = factory.getObject().getCurrentSession();
        return session.get(SurveyOption.class, optionId);
    }
}

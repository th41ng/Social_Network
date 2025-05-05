/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.service.impl;

import com.socialapp.pojo.Survey;
import com.socialapp.repository.SurveyRepository;
import com.socialapp.service.SurveyService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author DELL G15
 */
@Service
public class SurveyServiceImpl implements SurveyService{
    @Autowired
    private SurveyRepository surveyRepo;

    @Override
    public List<Survey> getSurveys(Map<String, String> params) {
        return surveyRepo.getSurveys(params);
    }

    @Override
    public Survey getSurveyById(int id) {
        return surveyRepo.getSurveyById(id);
    }

    @Override
    public Survey addOrUpdateSurvey(Survey s) {
        return surveyRepo.addOrUpdateSurvey(s);
    }

    @Override
    @Transactional
    public void deleteSurvey(int id) {
        surveyRepo.deleteSurvey(id);
    }
}

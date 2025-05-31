/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.service.impl;

import com.socialapp.pojo.SurveyResponse;
import com.socialapp.repository.SurveyResponseRepository;
import com.socialapp.service.SurveyResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author DELL G15
 */
@Service
public class SurveyResponseServiceImpl implements SurveyResponseService{
     @Autowired
    private SurveyResponseRepository surveyResponseRepository;

    @Override
    public List<SurveyResponse> getResponsesBySurveyId(int surveyId) {
        return surveyResponseRepository.getResponsesBySurveyId(surveyId);
    }

    @Override
    public SurveyResponse addSurveyResponse(SurveyResponse response) {
        return surveyResponseRepository.addSurveyResponse(response);
    }

    @Override
    public void deleteSurveyResponse(int responseId) {
        surveyResponseRepository.deleteSurveyResponse(responseId);
    }
    
     @Override
    public List<SurveyResponse> getResponsesByQuestionId(int questionId) {
        return surveyResponseRepository.getResponsesByQuestionId(questionId);  
    }
}

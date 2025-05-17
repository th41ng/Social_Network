/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.service.impl;

import com.socialapp.pojo.SurveyQuestion;
import com.socialapp.repository.SurveyQuestionRepository;
import com.socialapp.service.SurveyQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author DELL G15
 */
@Service
public class SurveyQuestionServiceImpl implements SurveyQuestionService {

    @Autowired
    private SurveyQuestionRepository surveyQuestionRepository;

    @Override
    public List<SurveyQuestion> getQuestionsBySurveyId(int surveyId) {
        return surveyQuestionRepository.getQuestionsBySurveyId(surveyId);
    }

    @Override
    public SurveyQuestion addSurveyQuestion(SurveyQuestion question) {
        return surveyQuestionRepository.addSurveyQuestion(question);
    }

    @Override
    public void deleteSurveyQuestion(int questionId) {
        surveyQuestionRepository.deleteSurveyQuestion(questionId);
    }

    @Override
    public SurveyQuestion getSurveyQuestionById(int questionId) {
        return surveyQuestionRepository.getSurveyQuestionById(questionId);
    }

    @Override
    public SurveyQuestion updateSurveyQuestion(SurveyQuestion question) {
        return surveyQuestionRepository.updateSurveyQuestion(question);  // Sửa câu hỏi
    }
}

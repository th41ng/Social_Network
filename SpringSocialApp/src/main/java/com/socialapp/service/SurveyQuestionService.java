/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.service;

import com.socialapp.pojo.SurveyQuestion;
import java.util.List;

/**
 *
 * @author DELL G15
 */
public interface SurveyQuestionService {

    List<SurveyQuestion> getQuestionsBySurveyId(int surveyId);

    SurveyQuestion addSurveyQuestion(SurveyQuestion question);

    void deleteSurveyQuestion(int questionId);

    SurveyQuestion getSurveyQuestionById(int questionId);
}

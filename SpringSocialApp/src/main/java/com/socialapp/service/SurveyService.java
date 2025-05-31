/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.service;

import com.socialapp.pojo.Survey;
import java.util.List;
import java.util.Map;

/**
 *
 * @author DELL G15
 */
public interface SurveyService {

    List<Survey> getSurveys(Map<String, String> params);

    Survey getSurveyById(int id);

    Survey addOrUpdateSurvey(Survey s);

    void deleteSurvey(int id);

    void toggleSurveyActiveState(int surveyId);

    long countSurveys(Map<String, String> params);

}

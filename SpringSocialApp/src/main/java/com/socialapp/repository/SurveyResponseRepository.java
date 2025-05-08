/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.repository;

import com.socialapp.pojo.SurveyResponse;
import java.util.List;

/**
 *
 * @author DELL G15
 */
public interface SurveyResponseRepository {

    List<SurveyResponse> getResponsesBySurveyId(int surveyId);

    SurveyResponse addSurveyResponse(SurveyResponse response);

    void deleteSurveyResponse(int responseId);

    List<SurveyResponse> getResponsesByQuestionId(int questionId);
}

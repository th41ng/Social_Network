package com.socialapp.repository;

import com.socialapp.pojo.SurveyQuestion;
import java.util.List;

public interface SurveyQuestionRepository {

    List<SurveyQuestion> getQuestionsBySurveyId(int surveyId);

    SurveyQuestion addSurveyQuestion(SurveyQuestion question);

    SurveyQuestion updateSurveyQuestion(SurveyQuestion question);

    void deleteSurveyQuestion(int questionId);

    SurveyQuestion getSurveyQuestionById(int questionId);

    long countQuestionsBySurveyId(int surveyId);

}

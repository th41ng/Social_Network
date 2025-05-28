/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.repository;

import com.socialapp.pojo.Survey;
import java.util.List;
import java.util.Map;

/**
 *
 * @author DELL G15
 */
public interface SurveyRepository {

    List<Survey> getSurveys(Map<String, String> params);
    
    long countSurveys(Map<String, String> params); // Thêm phương thức này

    Survey getSurveyById(int id);

    Survey addOrUpdateSurvey(Survey s);

    void deleteSurvey(int id);
}

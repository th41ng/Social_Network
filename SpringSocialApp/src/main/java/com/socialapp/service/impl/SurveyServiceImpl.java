/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.service.impl;

import com.socialapp.pojo.Survey;
import com.socialapp.repository.SurveyRepository;
import com.socialapp.service.SurveyService;
import jakarta.persistence.EntityNotFoundException;
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
    
     @Override
    @Transactional
    public void toggleSurveyActiveState(int surveyId) {
        Survey survey = surveyRepo.getSurveyById(surveyId);

        if (survey == null) {
            throw new EntityNotFoundException("Không tìm thấy khảo sát với ID: " + surveyId);
        }

        // Đảo ngược trạng thái isActive.
        // Nếu isActive là null (mặc dù DB có thể có default), coi như false để khi đảo sẽ thành true.
        boolean currentActiveState = survey.getIsActive() != null && survey.getIsActive();
        survey.setIsActive(!currentActiveState);

        surveyRepo.addOrUpdateSurvey(survey); // Sử dụng lại phương thức hiện có để lưu (merge sẽ cập nhật)
    }
    
    
}

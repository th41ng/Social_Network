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
public class SurveyServiceImpl implements SurveyService {
    @Autowired
    private SurveyRepository surveyRepo;

    @Override
    public List<Survey> getSurveys(Map<String, String> params) {
        return surveyRepo.getSurveys(params);
    }

    @Override
    public long countSurveys(Map<String, String> params) { 
        return surveyRepo.countSurveys(params);
    }

    @Override
    public Survey getSurveyById(int id) {
        return surveyRepo.getSurveyById(id);
    }

    @Override
    @Transactional 
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

        boolean currentActiveState = survey.getIsActive() != null && survey.getIsActive();
        survey.setIsActive(!currentActiveState);

        surveyRepo.addOrUpdateSurvey(survey); 
    }
}
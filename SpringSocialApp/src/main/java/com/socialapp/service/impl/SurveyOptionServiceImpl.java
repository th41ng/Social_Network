/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.service.impl;
import com.socialapp.pojo.SurveyOption;
import com.socialapp.repository.SurveyOptionRepository;
import com.socialapp.service.SurveyOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 *
 * @author DELL G15
 */
@Service
public class SurveyOptionServiceImpl implements SurveyOptionService {

    @Autowired
    private SurveyOptionRepository surveyOptionRepository;

    @Override
    public SurveyOption addSurveyOption(SurveyOption option) {
        return surveyOptionRepository.addSurveyOption(option);
    }

    @Override
    public void deleteSurveyOption(int optionId) {
        surveyOptionRepository.deleteSurveyOption(optionId);
    }

    @Override
    public SurveyOption getSurveyOptionById(int optionId) {
        return surveyOptionRepository.getSurveyOptionById(optionId);
    }
    
      @Override
    public SurveyOption updateSurveyOption(SurveyOption option) {
        return surveyOptionRepository.updateSurveyOption(option);  // Sửa lựa chọn
    }
    
    
}
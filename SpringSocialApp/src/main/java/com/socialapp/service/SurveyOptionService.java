/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.service;

import com.socialapp.pojo.SurveyOption;

/**
 *
 * @author DELL G15
 */
public interface SurveyOptionService {

    SurveyOption addSurveyOption(SurveyOption option);

    SurveyOption updateSurveyOption(SurveyOption option);

    void deleteSurveyOption(int optionId);

    SurveyOption getSurveyOptionById(int optionId);
}

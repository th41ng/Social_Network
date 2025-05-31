package com.socialapp.repository;

import com.socialapp.pojo.SurveyOption;

public interface SurveyOptionRepository {

    SurveyOption addSurveyOption(SurveyOption option);

    SurveyOption updateSurveyOption(SurveyOption option);

    void deleteSurveyOption(int optionId);

    SurveyOption getSurveyOptionById(int optionId);
}

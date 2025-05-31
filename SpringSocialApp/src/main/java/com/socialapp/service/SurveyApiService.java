package com.socialapp.service;

import com.socialapp.dto.SurveyClientListDTO;
import com.socialapp.dto.SurveyDetailClientDTO;

import com.socialapp.dto.SurveyResponseSubmitDTO;
import com.socialapp.pojo.User;
import java.util.List;
import java.util.Map;

public interface SurveyApiService {

    List<SurveyClientListDTO> getActiveSurveysForClient(Map<String, String> params, User currentUser);

    SurveyDetailClientDTO getSurveyDetailsForClient(int surveyId, User currentUser);

    boolean submitSurveyResponseForClient(int surveyId, SurveyResponseSubmitDTO responseSubmitDTO, User currentUser);
}

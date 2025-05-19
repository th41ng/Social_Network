package com.socialapp.service.impl;

import com.socialapp.dto.SurveyClientListDTO;
import com.socialapp.dto.SurveyDetailClientDTO;
import com.socialapp.dto.SurveyOptionClientDTO;
import com.socialapp.dto.SurveyQuestionClientDTO;
import com.socialapp.dto.SurveyResponseItemDTO;
import com.socialapp.dto.SurveyResponseSubmitDTO;
import com.socialapp.pojo.*;
import com.socialapp.repository.SurveyOptionRepository;
import com.socialapp.repository.SurveyQuestionRepository;
import com.socialapp.repository.SurveyRepository;
import com.socialapp.repository.SurveyResponseRepository;
import com.socialapp.service.SurveyApiService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SurveyApiServiceImpl implements SurveyApiService {

    private static final Logger logger = LoggerFactory.getLogger(SurveyApiServiceImpl.class);

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SurveyQuestionRepository surveyQuestionRepository;

    @Autowired
    private SurveyOptionRepository surveyOptionRepository;

    @Autowired
    private SurveyResponseRepository surveyResponseRepository;

    private SurveyOptionClientDTO convertToSurveyOptionClientDTO(SurveyOption option) {
        if (option == null) {
            return null;
        }
        SurveyOptionClientDTO dto = new SurveyOptionClientDTO();
        dto.setOptionId(option.getOptionId());
        dto.setOptionText(option.getOptionText());
        return dto;
    }

    private SurveyQuestionClientDTO convertToSurveyQuestionClientDTO(SurveyQuestion question) {
        if (question == null) {
            return null;
        }
        SurveyQuestionClientDTO dto = new SurveyQuestionClientDTO();
        dto.setQuestionId(question.getQuestionId());
        dto.setQuestionText(question.getQuestionText());

        if (question.getTypeId() != null) {
            dto.setQuestionType(question.getTypeId().getTypeName());
        } else {
            dto.setQuestionType("UNKNOWN");
        }

        if (question.getSurveyOptions() != null && !question.getSurveyOptions().isEmpty()) {
            dto.setOptions(
                question.getSurveyOptions().stream()
                    .map(this::convertToSurveyOptionClientDTO)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList())
            );
        }
        return dto;
    }

    private boolean hasUserResponded(Integer surveyId, Integer userId) {
        if (userId == null || surveyId == null) return false;
        List<SurveyResponse> responses = surveyResponseRepository.getResponsesBySurveyIdAndUserId(surveyId, userId);
        return responses != null && !responses.isEmpty();
    }

private SurveyClientListDTO convertToSurveyClientListDTO(Survey survey, User currentUser) {
    if (survey == null) {
        return null;
    }
    SurveyClientListDTO dto = new SurveyClientListDTO();
    dto.setSurveyId(survey.getSurveyId());
    dto.setTitle(survey.getTitle());
    dto.setDescription(survey.getDescription());

    if (survey.getCreatedAt() != null) {
        dto.setCreatedAt(survey.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    // Thêm logic để đếm số câu hỏi
    long questionCount = surveyQuestionRepository.countQuestionsBySurveyId(survey.getSurveyId());
    dto.setQuestionCount((int) questionCount);  // Đặt số câu hỏi vào DTO

    Boolean isActiveFromDb = survey.getIsActive();
    if (isActiveFromDb != null && isActiveFromDb) {
        dto.setStatus("ACTIVE");
    } else {
        dto.setStatus("INACTIVE");
    }

    if (currentUser != null && survey.getSurveyId() != null) {
        dto.setIsRespondedByCurrentUser(hasUserResponded(survey.getSurveyId(), currentUser.getId()));
    } else {
        dto.setIsRespondedByCurrentUser(false);
    }

    return dto;
}


    private SurveyDetailClientDTO convertToSurveyDetailClientDTO(Survey survey, User currentUser) {
        if (survey == null) {
            return null;
        }
        SurveyDetailClientDTO dto = new SurveyDetailClientDTO();
        dto.setSurveyId(survey.getSurveyId());
        dto.setTitle(survey.getTitle());
        dto.setDescription(survey.getDescription());

        List<SurveyQuestion> questionsForSurvey = surveyQuestionRepository.getQuestionsBySurveyId(survey.getSurveyId());
        if (questionsForSurvey != null) {
            dto.setQuestions(
                questionsForSurvey.stream()
                    .map(this::convertToSurveyQuestionClientDTO)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList())
            );
        }

        boolean canRespond = false;
        Boolean isActiveFromDb = survey.getIsActive();

        if (isActiveFromDb != null && isActiveFromDb) {
             if (currentUser != null && survey.getSurveyId() != null) {
                canRespond = !hasUserResponded(survey.getSurveyId(), currentUser.getId());
            } else if (currentUser == null) {
                canRespond = true;
            }
        } else {
            canRespond = false; // Không thể trả lời nếu survey không active
        }
        dto.setCanRespond(canRespond);
        // Bạn cũng có thể muốn set status cho SurveyDetailClientDTO nếu nó có trường đó
        // String statusForDetail = (isActiveFromDb != null && isActiveFromDb) ? "ACTIVE" : "INACTIVE";
        // if (dto has setStatus method) dto.setStatus(statusForDetail);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurveyClientListDTO> getActiveSurveysForClient(Map<String, String> params, User currentUser) {
        List<Survey> surveys = surveyRepository.getSurveys(params);
        return surveys.stream()
            .map(survey -> convertToSurveyClientListDTO(survey, currentUser))
            .filter(java.util.Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SurveyDetailClientDTO getSurveyDetailsForClient(int surveyId, User currentUser) {
        Survey survey = surveyRepository.getSurveyById(surveyId);
        if (survey == null) {
            logger.warn("Client attempted to access non-existent survey ID {}", surveyId);
            throw new EntityNotFoundException("Survey not found with ID: " + surveyId);
        }
        // Sau khi có isActive, bạn có thể muốn kiểm tra nếu survey không active thì không cho xem chi tiết
        // hoặc hiển thị thông báo phù hợp
        // if (survey.getIsActive() == null || !survey.getIsActive()) {
        //     logger.warn("Client attempted to access inactive survey ID {}", surveyId);
        //     throw new IllegalStateException("Survey is not active: " + surveyId); // Hoặc trả về DTO với trạng thái phù hợp
        // }
        return convertToSurveyDetailClientDTO(survey, currentUser);
    }

    @Override
    @Transactional
    public boolean submitSurveyResponseForClient(int surveyId, SurveyResponseSubmitDTO responseSubmitDTO, User currentUser) {
        Survey survey = surveyRepository.getSurveyById(surveyId);

        if (survey == null) {
            logger.warn("User {} attempt to respond to non-existent survey ID {}", (currentUser != null ? currentUser.getUsername() : "anonymous"), surveyId);
            throw new EntityNotFoundException("Survey not found or has been deleted: " + surveyId);
        }

        Boolean isActiveFromDb = survey.getIsActive();
        if (isActiveFromDb == null || !isActiveFromDb) {
            logger.warn("User {} attempt to respond to inactive survey ID {}", (currentUser != null ? currentUser.getUsername() : "anonymous"), surveyId);
            throw new IllegalStateException("Survey is not active and cannot be responded to: " + surveyId);
        }
        
        if (currentUser == null) {
            logger.warn("Anonymous user attempt to respond to survey ID {}", surveyId);
            // Nếu bạn quyết định không cho người dùng ẩn danh, hãy throw lỗi ở đây
            // throw new SecurityException("User must be logged in to respond.");
        } else {
            if (hasUserResponded(survey.getSurveyId(), currentUser.getId())) {
                logger.warn("User {} (ID: {}) already responded to survey ID {}.", currentUser.getUsername(), currentUser.getId(), surveyId);
                throw new IllegalStateException("You have already responded to this survey.");
            }
        }

        if (responseSubmitDTO.getResponses() == null || responseSubmitDTO.getResponses().isEmpty()) {
            logger.warn("Empty response submission for survey ID {} by user {}", surveyId, (currentUser != null ? currentUser.getUsername() : "anonymous"));
            throw new IllegalArgumentException("Response list cannot be empty.");
        }

        Date now = new Date();
        for (SurveyResponseItemDTO item : responseSubmitDTO.getResponses()) {
            SurveyQuestion question = surveyQuestionRepository.getSurveyQuestionById(item.getQuestionId());
            if (question == null || !question.getSurveyId().getSurveyId().equals(surveyId)) {
                logger.error("Invalid question ID {} submitted for survey ID {} by user {}", item.getQuestionId(), surveyId, (currentUser != null ? currentUser.getUsername() : "anonymous"));
                throw new IllegalArgumentException("Invalid question ID: " + item.getQuestionId() + " for this survey.");
            }

            if (item.getSelectedOptionId() != null) {
                SurveyOption option = surveyOptionRepository.getSurveyOptionById(item.getSelectedOptionId());
                if (option == null || !option.getQuestionId().getQuestionId().equals(question.getQuestionId())) {
                    throw new IllegalArgumentException("Invalid option ID: " + item.getSelectedOptionId() + " for question " + question.getQuestionId());
                }
                SurveyResponse response = new SurveyResponse();
                response.setSurveyId(survey);
                response.setQuestionId(question);
                response.setOptionId(option.getOptionId());
                response.setUserId(currentUser);
                response.setResponseAt(now);
                surveyResponseRepository.addSurveyResponse(response);

            } else if (item.getSelectedOptionIds() != null && !item.getSelectedOptionIds().isEmpty()) {
                for (Integer optionIdValue : item.getSelectedOptionIds()) {
                    SurveyOption option = surveyOptionRepository.getSurveyOptionById(optionIdValue);
                    if (option == null || !option.getQuestionId().getQuestionId().equals(question.getQuestionId())) {
                        throw new IllegalArgumentException("Invalid option ID in list: " + optionIdValue + " for question " + question.getQuestionId());
                    }
                    SurveyResponse response = new SurveyResponse();
                    response.setSurveyId(survey);
                    response.setQuestionId(question);
                    response.setOptionId(option.getOptionId());
                    response.setUserId(currentUser);
                    response.setResponseAt(now);
                    surveyResponseRepository.addSurveyResponse(response);
                }
            } else if (item.getResponseText() != null && !item.getResponseText().trim().isEmpty()) {
                SurveyResponse response = new SurveyResponse();
                response.setSurveyId(survey);
                response.setQuestionId(question);
                response.setResponseText(item.getResponseText().trim());
                response.setUserId(currentUser);
                response.setResponseAt(now);
                surveyResponseRepository.addSurveyResponse(response);
            } else {
                String questionTypeStr = question.getTypeId() != null ? question.getTypeId().getTypeName() : "UNKNOWN";
                logger.warn("No valid response provided for question ID {} (type: {}) in survey ID {} by user {}",
                    question.getQuestionId(), questionTypeStr, surveyId, (currentUser != null ? currentUser.getUsername() : "anonymous"));
            }
        }
        logger.info("User {} (ID: {}) successfully submitted responses for survey ID {}", (currentUser != null ? currentUser.getUsername() : "anonymous"), (currentUser != null ? currentUser.getId() : "N/A"), surveyId);
        return true;
    }
}
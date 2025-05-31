package com.socialapp.controllers;

import com.socialapp.dto.SurveyClientListDTO;
import com.socialapp.dto.SurveyDetailClientDTO;
import com.socialapp.dto.SurveyResponseSubmitDTO;
import com.socialapp.pojo.User; 
import com.socialapp.service.SurveyApiService; 
import com.socialapp.service.SurveyService;   
import com.socialapp.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/surveys") 
@CrossOrigin
public class ApiSurveyController {

    private static final Logger logger = LoggerFactory.getLogger(ApiSurveyController.class);

    @Autowired
    private SurveyApiService surveyApiService; 

    @Autowired
    private SurveyService surveyService;   

    @Autowired
    private UserService userService;

    
    @GetMapping
    public ResponseEntity<?> listSurveysForClient(
            @RequestParam(required = false) Map<String, String> params,
            Authentication authentication) {
        
        User currentUser = getCurrentUserFromAuthentication(authentication);
        logger.info("Client (user: {}) yêu cầu danh sách khảo sát (DTO) với params: {}", 
                    (currentUser != null ? currentUser.getUsername() : "anonymous"), params);
        try {
            List<SurveyClientListDTO> surveys = surveyApiService.getActiveSurveysForClient(params, currentUser);
            if (surveys == null || surveys.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            return ResponseEntity.ok(surveys);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách khảo sát cho client (user: {}): {}", 
                         (currentUser != null ? currentUser.getUsername() : "anonymous"), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "Không thể tải danh sách khảo sát."));
        }
    }

   
    @GetMapping("/{surveyId}")
    public ResponseEntity<?> getSurveyDetailsForClient(
            @PathVariable("surveyId") int surveyId,
            Authentication authentication) {
        
        User currentUser = getCurrentUserFromAuthentication(authentication);
        logger.info("Client (user: {}) yêu cầu chi tiết khảo sát (DTO) ID {} để trả lời", 
                    (currentUser != null ? currentUser.getUsername() : "anonymous"), surveyId);
        try {
            SurveyDetailClientDTO surveyDetail = surveyApiService.getSurveyDetailsForClient(surveyId, currentUser);
            return ResponseEntity.ok(surveyDetail);
        } catch (EntityNotFoundException e) {
            logger.warn("Client (user: {}) yêu cầu khảo sát ID {} không tồn tại: {}", 
                         (currentUser != null ? currentUser.getUsername() : "anonymous"), surveyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) { 
            logger.warn("Client (user: {}) không thể truy cập khảo sát ID {}: {}", 
                         (currentUser != null ? currentUser.getUsername() : "anonymous"), surveyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } 
        catch (Exception e) {
            logger.error("Lỗi khi lấy chi tiết khảo sát ID {} cho client (user: {}): {}", 
                         surveyId, (currentUser != null ? currentUser.getUsername() : "anonymous"), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "Lỗi máy chủ khi lấy chi tiết khảo sát."));
        }
    }

  
    @PostMapping("/{surveyId}/responses") 
    public ResponseEntity<?> submitClientSurveyResponses(
            @PathVariable("surveyId") int surveyId,
            @RequestBody SurveyResponseSubmitDTO responseSubmitDTO, 
            Authentication authentication) {
        
        User currentUser = null;
        String usernameForLog = "anonymous";

        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal().toString())) {
            String username = getUsernameFromPrincipal(authentication.getPrincipal());
            if (username != null) {
                currentUser = userService.getUserByUsername(username);
                usernameForLog = username;
                if (currentUser == null) {
                     logger.error("DB User not found: {} for survey response ID: {}", username, surveyId);
                     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Lỗi thông tin người dùng hệ thống."));
                }
            } else {
                 logger.error("Cannot get username from principal for survey response ID: {}", surveyId);
                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Thông tin xác thực không hợp lệ."));
            }
        }

        logger.info("Client (user: {}) submitting responses for survey ID: {}", usernameForLog, surveyId);

        try {
            boolean success = surveyApiService.submitSurveyResponseForClient(surveyId, responseSubmitDTO, currentUser);
            if (success) {
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Cảm ơn bạn đã hoàn thành khảo sát!"));
            } else {
               
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Không thể xử lý phản hồi của bạn do dữ liệu không hợp lệ hoặc lỗi đã xảy ra."));
            }
        } catch (EntityNotFoundException e) {
            logger.warn("Error submitting response for survey ID {} (user: {}): {}", surveyId, usernameForLog, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) { 
            logger.warn("State error submitting response for survey ID {} (user: {}): {}", surveyId, usernameForLog, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) { 
            logger.warn("Invalid data submitting response for survey ID {} (user: {}): {}", surveyId, usernameForLog, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error submitting response for survey ID {} (user: {}): {}", surveyId, usernameForLog, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Lỗi máy chủ khi xử lý phản hồi."));
        }
    }
    
    @DeleteMapping("/delete/{surveyId}")
    public ResponseEntity<Void> deleteSurveyByAdmin(@PathVariable(value = "surveyId") int id, Authentication authentication) {
       
        User currentUser = getCurrentUserFromAuthentication(authentication); 
        String usernameForLog = currentUser != null ? currentUser.getUsername() : "unknown_admin_attempt";

        try {
            logger.info("Admin action (user: {}): Attempting to delete survey with ID: {}", usernameForLog, id);
            this.surveyService.deleteSurvey(id);
            logger.info("Admin action (user: {}): Successfully deleted survey with ID: {}", usernameForLog, id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e){ 
             logger.warn("Admin action (user: {}): Survey with ID: {} not found for deletion. Error: {}",usernameForLog, id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } 
        catch (Exception e) {
            logger.error("Admin action (user: {}): Error deleting survey with ID: {}. Details: ", usernameForLog, id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper methods
    private User getCurrentUserFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal().toString())) {
            String username = getUsernameFromPrincipal(authentication.getPrincipal());
            if (username != null) {
                try {
                    return userService.getUserByUsername(username);
                } catch (Exception e) {
                    logger.error("Cannot get User from username: {} in Authentication - Error: {}", username, e.getMessage());
                    return null;
                }
            }
        }
        return null;
    }
    
    private String getUsernameFromPrincipal(Object principal) {
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }
        logger.debug("Principal type for getUsernameFromPrincipal: {}", (principal != null ? principal.getClass().getName() : "null"));
        return principal != null ? principal.toString() : null;
    }
}
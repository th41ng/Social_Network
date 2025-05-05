package com.socialapp.controllers;

import com.socialapp.pojo.Survey;
import com.socialapp.service.SurveyService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DELL G15
 */
@RestController
@RequestMapping("/api")
public class ApiSurveyController {
    @Autowired
    private SurveyService surveyService;

    // Khởi tạo LOG
    private static final Logger logger = LoggerFactory.getLogger(ApiSurveyController.class);

    @DeleteMapping("/surveys/{surveyId}")
    public ResponseEntity<Void> deleteSurvey(@PathVariable(value = "surveyId") int id) {
        try {
            logger.info("Đang cố gắng xóa khảo sát với ID: {}", id); 
            this.surveyService.deleteSurvey(id);
            logger.info("Đã xóa thành công khảo sát với ID: {}", id); 
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            // !!! GHI LẠI EXCEPTION VÀO LOG !!!
            logger.error("Lỗi khi xóa khảo sát với ID: {}. Chi tiết lỗi:", id, e); 
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/surveys")
    public ResponseEntity<List<Survey>> listSurveys(@RequestParam Map<String, String> params) {
        
        return new ResponseEntity<>(this.surveyService.getSurveys(params), HttpStatus.OK);
    }

    @GetMapping("/surveys/{surveyId}")
    public ResponseEntity<Survey> getSurvey(@PathVariable(value = "surveyId") int id) {
        
        return new ResponseEntity<>(this.surveyService.getSurveyById(id), HttpStatus.OK);
    }
}
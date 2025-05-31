/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;

import com.socialapp.pojo.Survey;
import com.socialapp.pojo.SurveyQuestion;
import com.socialapp.pojo.QuestionType;
import com.socialapp.pojo.SurveyOption;
import com.socialapp.service.SurveyQuestionService;
import com.socialapp.service.SurveyService;
import com.socialapp.service.QuestionTypeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author DELL G15
 */
@Controller
@RequestMapping("/questions")
public class SurveyQuestionController {

    private static final Logger logger = LoggerFactory.getLogger(SurveyQuestionController.class);

    // Định nghĩa hằng số cho ID loại câu hỏi trắc nghiệm
    private static final Integer MULTIPLE_CHOICE_TYPE_ID = 1;

    @Autowired
    private SurveyQuestionService surveyQuestionService;

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private QuestionTypeService questionTypeService;

    private boolean isMultipleChoiceType(QuestionType type) {
        if (type == null) {
            return false;
        }

        return Objects.equals(type.getTypeId(), MULTIPLE_CHOICE_TYPE_ID);
    }

    @GetMapping("/{surveyId}")
    public String listQuestions(@PathVariable("surveyId") int surveyId, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Request to list questions for surveyId: {}", surveyId);
        Survey survey = surveyService.getSurveyById(surveyId);
        if (survey == null) {
            logger.warn("Survey with id {} not found.", surveyId);
            redirectAttributes.addFlashAttribute("errorMessage", "Khảo sát ID " + surveyId + " không tồn tại.");
            return "redirect:/surveys";
        }
        List<SurveyQuestion> questions = surveyQuestionService.getQuestionsBySurveyId(surveyId);
        model.addAttribute("survey", survey);
        model.addAttribute("questions", questions);
        logger.info("Found {} questions for surveyId: {}", questions.size(), surveyId);
        return "question_management";
    }

    @GetMapping("/add/{surveyId}")
    public String addQuestionForm(@PathVariable("surveyId") int surveyId, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Request to show add question form for surveyId: {}", surveyId);
        Survey survey = surveyService.getSurveyById(surveyId);
        if (survey == null) {
            logger.warn("Survey with id {} not found for adding question.", surveyId);
            redirectAttributes.addFlashAttribute("errorMessage", "Khảo sát không tồn tại để thêm câu hỏi.");
            return "redirect:/surveys";
        }
        SurveyQuestion newQuestion = new SurveyQuestion();
        model.addAttribute("survey", survey);
        model.addAttribute("question", newQuestion);
        model.addAttribute("questionTypes", this.questionTypeService.getQuestionTypes());
        return "question_form";
    }

    @GetMapping("/edit/{questionId}")
    public String editQuestionForm(@PathVariable("questionId") int questionId, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Request to show edit question form for questionId: {}", questionId);
        SurveyQuestion question = surveyQuestionService.getSurveyQuestionById(questionId);
        if (question == null) {
            logger.warn("Question with id {} not found for editing.", questionId);
            redirectAttributes.addFlashAttribute("errorMessage", "Câu hỏi không tồn tại để chỉnh sửa.");
            return "redirect:/surveys";
        }
        if (question.getSurveyId() == null) {
            logger.warn("Question with id {} does not belong to any survey.", questionId);
            redirectAttributes.addFlashAttribute("errorMessage", "Câu hỏi này không thuộc về khảo sát nào.");
            return "redirect:/surveys";
        }

        model.addAttribute("question", question);
        model.addAttribute("survey", question.getSurveyId());
        model.addAttribute("questionTypes", this.questionTypeService.getQuestionTypes());
        return "question_form";
    }

    @PostMapping("/save")
    public String saveQuestion(@ModelAttribute("question") @Valid SurveyQuestion questionFromForm,
            BindingResult result,
            @RequestParam("surveyObjectId") int surveyObjectId,
            @RequestParam("selectedTypeId") int selectedTypeId,
            Model model, RedirectAttributes redirectAttributes) {
        logger.info("Attempting to save question for surveyObjectId: {}, selectedTypeId: {}", surveyObjectId, selectedTypeId);

        Survey survey = surveyService.getSurveyById(surveyObjectId);
        if (survey == null) {
            logger.warn("Invalid surveyObjectId: {} provided during question save.", surveyObjectId);
            model.addAttribute("errorMessage", "Khảo sát không hợp lệ hoặc không tồn tại.");
            model.addAttribute("questionTypes", this.questionTypeService.getQuestionTypes());
            model.addAttribute("question", questionFromForm); // Giữ lại dữ liệu form
            return "question_form";
        }

        QuestionType selectedQuestionType = this.questionTypeService.getQuestionTypeById(selectedTypeId);
        if (selectedQuestionType == null) {
            logger.warn("Invalid selectedTypeId: {} provided during question save.", selectedTypeId);
            result.rejectValue("typeId", "invalid.typeId", "Loại câu hỏi không hợp lệ.");
        } else {
            questionFromForm.setTypeId(selectedQuestionType);
        }

        if (result.hasErrors()) {
            logger.warn("Validation errors found when saving question: {}", result.getAllErrors());
            model.addAttribute("errorMessage", "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.");
            model.addAttribute("questionTypes", this.questionTypeService.getQuestionTypes());
            model.addAttribute("survey", survey);
            model.addAttribute("question", questionFromForm);
            return "question_form";
        }

        boolean isMcq = isMultipleChoiceType(selectedQuestionType);

        if (isMcq && (questionFromForm.getQuestionId() == null)
                && (questionFromForm.getSurveyOptions() == null
                || questionFromForm.getSurveyOptions().stream()
                        .allMatch(opt -> opt == null || opt.getOptionText() == null || opt.getOptionText().trim().isEmpty()))) {
            logger.warn("MCQ question submitted without any valid options for surveyId: {}", surveyObjectId);
            model.addAttribute("errorMessage", "Câu hỏi trắc nghiệm phải có ít nhất một lựa chọn.");
            model.addAttribute("questionTypes", this.questionTypeService.getQuestionTypes());
            model.addAttribute("survey", survey);
            model.addAttribute("question", questionFromForm);
            return "question_form";
        }

        SurveyQuestion questionToSave;
        boolean isUpdate = questionFromForm.getQuestionId() != null && questionFromForm.getQuestionId() > 0;

        if (isUpdate) {
            logger.info("Updating existing question with id: {}", questionFromForm.getQuestionId());
            questionToSave = surveyQuestionService.getSurveyQuestionById(questionFromForm.getQuestionId());
            if (questionToSave == null) {
                logger.warn("Question with id {} not found for update.", questionFromForm.getQuestionId());
                redirectAttributes.addFlashAttribute("errorMessage", "Câu hỏi cần cập nhật không tồn tại.");
                return "redirect:/questions/" + surveyObjectId;
            }
        } else {
            logger.info("Adding new question for surveyId: {}", surveyObjectId);
            questionToSave = new SurveyQuestion();
        }

        questionToSave.setQuestionText(questionFromForm.getQuestionText());
        questionToSave.setIsRequired(questionFromForm.getIsRequired());
        questionToSave.setQuestionOrder(questionFromForm.getQuestionOrder());
        questionToSave.setSurveyId(survey);
        questionToSave.setTypeId(selectedQuestionType);

        if (isMcq) {
            logger.debug("Processing options for MCQ question (id: {})", questionToSave.getQuestionId());
            List<SurveyOption> optionsFromFormFiltered = new ArrayList<>();
            if (questionFromForm.getSurveyOptions() != null) {
                for (SurveyOption optForm : questionFromForm.getSurveyOptions()) {
                    if (optForm != null && optForm.getOptionText() != null && !optForm.getOptionText().trim().isEmpty()) {
                        optionsFromFormFiltered.add(optForm);
                    }
                }
            }
            logger.debug("Filtered options from form: {} options", optionsFromFormFiltered.size());

            if (isUpdate) {

                List<SurveyOption> persistedOptions = new ArrayList<>(questionToSave.getSurveyOptions() != null ? questionToSave.getSurveyOptions() : List.of());
                List<SurveyOption> finalOptionsToKeep = new ArrayList<>();

                for (SurveyOption optForm : optionsFromFormFiltered) {
                    optForm.setQuestionId(questionToSave);

                    if (optForm.getOptionId() != null) {
                        SurveyOption existingOpt = persistedOptions.stream()
                                .filter(pOpt -> pOpt.getOptionId().equals(optForm.getOptionId()))
                                .findFirst()
                                .orElse(null);

                        if (existingOpt != null) {
                            logger.trace("Updating existing option id: {}, text: '{}'", existingOpt.getOptionId(), optForm.getOptionText().trim());
                            existingOpt.setOptionText(optForm.getOptionText().trim());
                            finalOptionsToKeep.add(existingOpt);
                            persistedOptions.remove(existingOpt);
                        } else {

                            logger.trace("Option from form with id: {} not found in persisted. Treating as new.", optForm.getOptionId());
                            optForm.setOptionId(null);
                            finalOptionsToKeep.add(optForm);
                        }
                    } else {
                        logger.trace("Adding new option with text: '{}'", optForm.getOptionText());
                        finalOptionsToKeep.add(optForm);
                    }
                }

                if (questionToSave.getSurveyOptions() == null) {
                    questionToSave.setSurveyOptions(new ArrayList<>());
                }
                questionToSave.getSurveyOptions().clear();
                questionToSave.getSurveyOptions().addAll(finalOptionsToKeep);
                logger.debug("Final options for update: {} options", finalOptionsToKeep.size());

            } else {
                List<SurveyOption> newOptions = new ArrayList<>();
                for (SurveyOption optForm : optionsFromFormFiltered) {
                    optForm.setOptionId(null);
                    optForm.setQuestionId(questionToSave);
                    newOptions.add(optForm);
                }
                questionToSave.setSurveyOptions(newOptions);
                logger.debug("Set new options for new question: {} options", newOptions.size());
            }
        } else {
            logger.debug("Not an MCQ question, clearing/initializing options for question (id: {})", questionToSave.getQuestionId());
            if (questionToSave.getSurveyOptions() != null) {
                questionToSave.getSurveyOptions().clear();
            } else {
                questionToSave.setSurveyOptions(new ArrayList<>());
            }
        }

        try {
            if (isUpdate) {
                surveyQuestionService.updateSurveyQuestion(questionToSave);
                logger.info("Successfully updated question with id: {}", questionToSave.getQuestionId());
            } else {
                surveyQuestionService.addSurveyQuestion(questionToSave);
                logger.info("Successfully added new question, assigned id: {}", questionToSave.getQuestionId());
            }
            redirectAttributes.addFlashAttribute("successMessage", (isUpdate ? "Cập nhật" : "Thêm") + " câu hỏi thành công!");
        } catch (Exception e) {

            logger.error("Error saving question for surveyId {}: {}", surveyObjectId, e.getMessage(), e);
            model.addAttribute("errorMessage", "Lỗi hệ thống khi lưu câu hỏi: " + e.getMessage());
            model.addAttribute("questionTypes", this.questionTypeService.getQuestionTypes());
            model.addAttribute("survey", survey);
            model.addAttribute("question", questionToSave);
            return "question_form";
        }

        return "redirect:/questions/" + surveyObjectId;
    }

    @DeleteMapping("/delete/{questionId}")
    @ResponseBody

    public ResponseEntity<Map<String, String>> deleteQuestion(@PathVariable("questionId") int questionId) {
        logger.info("Request to delete question with id: {}", questionId);
        SurveyQuestion question = surveyQuestionService.getSurveyQuestionById(questionId);
        if (question == null) {
            logger.warn("Question with id {} not found for deletion.", questionId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "Câu hỏi không tìm thấy"));
        }
        try {
            surveyQuestionService.deleteSurveyQuestion(questionId);
            logger.info("Successfully deleted question with id: {}", questionId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Xóa câu hỏi thành công"));
        } catch (Exception e) {
            logger.error("Error deleting question with id {}: {}", questionId, e.getMessage(), e);

            String safeErrorMessage = "Lỗi khi xóa câu hỏi. Vui lòng thử lại.";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", safeErrorMessage));
        }
    }
}

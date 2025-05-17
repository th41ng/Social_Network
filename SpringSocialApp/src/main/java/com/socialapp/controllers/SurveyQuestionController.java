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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SurveyQuestionService surveyQuestionService;

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private QuestionTypeService questionTypeService;

    private boolean isMultipleChoiceType(QuestionType type) {
        if (type == null || type.getTypeName() == null) {
            return false;
        }
        String typeNameLower = type.getTypeName().toLowerCase();

        return type.getTypeId() != null && type.getTypeId().equals(1);
    }

    @GetMapping("/{surveyId}")
    public String listQuestions(@PathVariable("surveyId") int surveyId, Model model, RedirectAttributes redirectAttributes) {
        Survey survey = surveyService.getSurveyById(surveyId);
        if (survey == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Khảo sát ID " + surveyId + " không tồn tại.");
            return "redirect:/surveys";
        }
        List<SurveyQuestion> questions = surveyQuestionService.getQuestionsBySurveyId(surveyId);
        model.addAttribute("survey", survey);
        model.addAttribute("questions", questions);
        return "question_management";
    }

    @GetMapping("/add/{surveyId}")
    public String addQuestionForm(@PathVariable("surveyId") int surveyId, Model model, RedirectAttributes redirectAttributes) {
        Survey survey = surveyService.getSurveyById(surveyId);
        if (survey == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Khảo sát không tồn tại để thêm câu hỏi.");
            return "redirect:/surveys";
        }
        SurveyQuestion newQuestion = new SurveyQuestion(); // surveyOptions đã được khởi tạo là new ArrayList<>() trong POJO
        model.addAttribute("survey", survey);
        model.addAttribute("question", newQuestion);
        model.addAttribute("questionTypes", this.questionTypeService.getQuestionTypes());
        return "question_form";
    }

    @GetMapping("/edit/{questionId}")
    public String editQuestionForm(@PathVariable("questionId") int questionId, Model model, RedirectAttributes redirectAttributes) {
        SurveyQuestion question = surveyQuestionService.getSurveyQuestionById(questionId);
        if (question == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Câu hỏi không tồn tại để chỉnh sửa.");
            return "redirect:/surveys";
        }
        if (question.getSurveyId() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Câu hỏi này không thuộc về khảo sát nào.");
            return "redirect:/surveys";
        }

        if (question.getSurveyOptions() != null) {
            question.getSurveyOptions().size();
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

        Survey survey = surveyService.getSurveyById(surveyObjectId);
        if (survey == null) {
            model.addAttribute("errorMessage", "Khảo sát không hợp lệ hoặc không tồn tại.");

            model.addAttribute("questionTypes", this.questionTypeService.getQuestionTypes());
            model.addAttribute("question", questionFromForm); // Trả lại dữ liệu đã nhập

            return "question_form";
        }

        QuestionType selectedQuestionType = this.questionTypeService.getQuestionTypeById(selectedTypeId);
        if (selectedQuestionType == null) {
            result.rejectValue("typeId", "invalid.typeId", "Loại câu hỏi không hợp lệ.");
        } else {
            questionFromForm.setTypeId(selectedQuestionType); // Gán typeId vào questionFromForm
        }

        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.");
            model.addAttribute("questionTypes", this.questionTypeService.getQuestionTypes());
            model.addAttribute("survey", survey);
            return "question_form";
        }

        // Validation: Nếu là trắc nghiệm mà không có option nào được gửi lên (cho câu hỏi mới)
        boolean isMcq = isMultipleChoiceType(selectedQuestionType);
        if (isMcq && (questionFromForm.getQuestionId() == null)
                && (questionFromForm.getSurveyOptions() == null
                || questionFromForm.getSurveyOptions().stream().allMatch(opt -> opt == null || opt.getOptionText() == null || opt.getOptionText().trim().isEmpty()))) {
            model.addAttribute("errorMessage", "Câu hỏi trắc nghiệm phải có ít nhất một lựa chọn.");
            model.addAttribute("questionTypes", this.questionTypeService.getQuestionTypes());
            model.addAttribute("survey", survey);
            model.addAttribute("question", questionFromForm);
            return "question_form";
        }

        SurveyQuestion questionToSave;
        boolean isUpdate = questionFromForm.getQuestionId() != null && questionFromForm.getQuestionId() > 0;

        if (isUpdate) {
            questionToSave = surveyQuestionService.getSurveyQuestionById(questionFromForm.getQuestionId());
            if (questionToSave == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Câu hỏi cần cập nhật không tồn tại.");
                return "redirect:/questions/" + surveyObjectId;
            }
        } else {
            questionToSave = new SurveyQuestion(); // surveyOptions đã được khởi tạo là new ArrayList<>() trong POJO
        }

        // Cập nhật các thuộc tính của questionToSave từ questionFromForm
        questionToSave.setQuestionText(questionFromForm.getQuestionText());
        questionToSave.setIsRequired(questionFromForm.getIsRequired());
        questionToSave.setQuestionOrder(questionFromForm.getQuestionOrder());
        questionToSave.setSurveyId(survey);
        questionToSave.setTypeId(selectedQuestionType);

        // === Xử lý SurveyOptions ===
        if (isMcq) {
            // Lấy danh sách option được gửi từ form, lọc bỏ những option rỗng
            List<SurveyOption> optionsFromFormFiltered = new ArrayList<>();
            if (questionFromForm.getSurveyOptions() != null) {
                for (SurveyOption optForm : questionFromForm.getSurveyOptions()) {
                    if (optForm != null && optForm.getOptionText() != null && !optForm.getOptionText().trim().isEmpty()) {
                        optionsFromFormFiltered.add(optForm);
                    }
                }
            }

            if (isUpdate) {

                List<SurveyOption> persistedOptions = new ArrayList<>(questionToSave.getSurveyOptions());
                List<SurveyOption> finalOptionsToKeep = new ArrayList<>();

                for (SurveyOption optForm : optionsFromFormFiltered) {
                    optForm.setQuestionId(questionToSave); // Quan trọng: thiết lập quan hệ hai chiều

                    if (optForm.getOptionId() != null) {
                        SurveyOption existingOpt = persistedOptions.stream()
                                .filter(pOpt -> pOpt.getOptionId().equals(optForm.getOptionId()))
                                .findFirst()
                                .orElse(null);

                        if (existingOpt != null) {
                            existingOpt.setOptionText(optForm.getOptionText().trim());
                            finalOptionsToKeep.add(existingOpt);
                            persistedOptions.remove(existingOpt);
                        } else { // Có ID từ form nhưng không có trong DB (bất thường), coi như option mới
                            optForm.setOptionId(null); // Bỏ ID để Hibernate tạo mới
                            finalOptionsToKeep.add(optForm);
                        }
                    } else { // Option mới từ form (không có ID)
                        finalOptionsToKeep.add(optForm);
                    }
                }
                // Sau vòng lặp:

                questionToSave.getSurveyOptions().clear();
                questionToSave.getSurveyOptions().addAll(finalOptionsToKeep);

            } else {
                List<SurveyOption> newOptions = new ArrayList<>();
                for (SurveyOption optForm : optionsFromFormFiltered) {
                    optForm.setOptionId(null);
                    optForm.setQuestionId(questionToSave);
                    newOptions.add(optForm);
                }
                questionToSave.setSurveyOptions(newOptions);
            }
        } else { // Không phải loại câu hỏi trắc nghiệm
            if (questionToSave.getSurveyOptions() != null) {
                questionToSave.getSurveyOptions().clear(); // Xóa hết option nếu có
            } else {
                questionToSave.setSurveyOptions(new ArrayList<>());
            }
        }

        try {
            if (isUpdate) {
                surveyQuestionService.updateSurveyQuestion(questionToSave);
            } else {
                surveyQuestionService.addSurveyQuestion(questionToSave);
            }
            redirectAttributes.addFlashAttribute("successMessage", (isUpdate ? "Cập nhật" : "Thêm") + " câu hỏi thành công!");
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu câu hỏi: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Lỗi hệ thống khi lưu câu hỏi: " + e.getMessage());
            model.addAttribute("questionTypes", this.questionTypeService.getQuestionTypes());
            model.addAttribute("survey", survey);
            model.addAttribute("question", questionToSave);
            return "question_form";
        }

        return "redirect:/questions/" + surveyObjectId;
    }

    @DeleteMapping("/delete/{questionId}")
    @ResponseBody // Trả về JSON cho AJAX call từ main.js (nếu hàm deleteSurvey của bạn gọi endpoint này)
    public String deleteQuestion(@PathVariable("questionId") int questionId) {
        SurveyQuestion question = surveyQuestionService.getSurveyQuestionById(questionId);
        if (question == null) {

            return "{\"status\":\"error\", \"message\":\"Câu hỏi không tìm thấy\"}";
        }
        try {
            surveyQuestionService.deleteSurveyQuestion(questionId);
            return "{\"status\":\"success\", \"message\":\"Xóa câu hỏi thành công\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\", \"message\":\"Lỗi khi xóa câu hỏi: " + e.getMessage().replace("\"", "'") + "\"}";
        }
    }

}

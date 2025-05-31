/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.service;

import com.socialapp.pojo.QuestionType;
import java.util.List;

/**
 *
 * @author DELL G15
 */
public interface QuestionTypeService {

    List<QuestionType> getQuestionTypes();

    QuestionType getQuestionTypeById(int typeId);
}

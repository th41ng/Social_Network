/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.repository;

import com.socialapp.pojo.QuestionType;
import java.util.List;

/**
 *
 * @author DELL G15
 */
public interface QuestionTypeRepository {

    List<QuestionType> findAll();

    QuestionType findById(int typeId);
}

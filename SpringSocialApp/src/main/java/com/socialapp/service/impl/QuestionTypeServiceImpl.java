package com.socialapp.service.impl;

import com.socialapp.pojo.QuestionType;
import com.socialapp.repository.QuestionTypeRepository;
import com.socialapp.service.QuestionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class QuestionTypeServiceImpl implements QuestionTypeService {

    @Autowired
    private QuestionTypeRepository questionTypeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<QuestionType> getQuestionTypes() {
        return this.questionTypeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionType getQuestionTypeById(int typeId) {
        return this.questionTypeRepository.findById(typeId);
    }
}

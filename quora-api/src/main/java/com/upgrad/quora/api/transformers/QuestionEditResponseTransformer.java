package com.upgrad.quora.api.transformers;

import com.upgrad.quora.api.model.QuestionEditResponse;
import com.upgrad.quora.service.common.Constants;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Component;

@Component
public class QuestionEditResponseTransformer {
    public QuestionEditResponse transform(QuestionEntity updatedQuestionEntity){
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(updatedQuestionEntity.getUuid()).status(Constants.QUESTION_UPDATED);
        return questionEditResponse;
    }
}

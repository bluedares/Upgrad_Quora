package com.upgrad.quora.api.transformers;

import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.common.Constants;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Component;

@Component
public class CreateQuestionResponseTransformer {
    public QuestionResponse transform(QuestionEntity questionEntity){
        QuestionResponse questionResponse = new QuestionResponse().id(questionEntity.getUuid()).status(Constants.QUESTION_CREATED);
        return questionResponse;
    }
}

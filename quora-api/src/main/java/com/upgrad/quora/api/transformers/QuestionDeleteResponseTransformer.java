package com.upgrad.quora.api.transformers;

import com.upgrad.quora.api.model.QuestionDeleteResponse;
import com.upgrad.quora.service.common.Constants;
import org.springframework.stereotype.Component;

@Component
public class QuestionDeleteResponseTransformer {
    public QuestionDeleteResponse transform(String questionId){
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(questionId).status(Constants.QUESTION_DELETED);
        return questionDeleteResponse;
    }
}

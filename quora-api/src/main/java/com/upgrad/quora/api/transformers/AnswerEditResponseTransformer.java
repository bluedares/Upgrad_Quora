package com.upgrad.quora.api.transformers;

import com.upgrad.quora.api.model.AnswerEditResponse;
import com.upgrad.quora.service.common.Constants;
import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Component;

@Component
public class AnswerEditResponseTransformer {
    public AnswerEditResponse transform(AnswerEntity answerEntity){
        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(answerEntity.getUuid()).status(Constants.ANSWER_UPDATED);
        return answerEditResponse;
    }
}

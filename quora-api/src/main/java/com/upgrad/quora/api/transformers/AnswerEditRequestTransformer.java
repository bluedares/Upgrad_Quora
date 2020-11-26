package com.upgrad.quora.api.transformers;

import com.upgrad.quora.api.model.AnswerEditRequest;
import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Component;

@Component
public class AnswerEditRequestTransformer {
    public AnswerEntity transform(AnswerEditRequest answerEditRequest,String answerUuid){
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(answerUuid);
        answerEntity.setAnswer(answerEditRequest.getContent());
        return answerEntity;
    }
}

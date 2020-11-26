package com.upgrad.quora.api.transformers;

import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Component;

@Component
public class AnswerRequestTransformer {
    public AnswerEntity transform(AnswerRequest answerRequest){
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAnswer(answerRequest.getAnswer());
        return answerEntity;
    }
}

package com.upgrad.quora.api.transformers;


import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.common.Constants;
import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Component;

@Component
public class AnswerResponseTransformer {
    public AnswerResponse transform(AnswerEntity answerEntity){
        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status(Constants.ANSWER_CREATED);
        return answerResponse;
    }
}

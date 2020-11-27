package com.upgrad.quora.api.transformers;


import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.common.Constants;
import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Component;

@Component
public class AnswerResponseTransformer {
    /**
     *
     * @param answerEntity
     * @return
     * This method transforms the answer entity object to create answer response object (which contains id of the answer created and its status).
     */
    public AnswerResponse transform(AnswerEntity answerEntity){
        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status(Constants.ANSWER_CREATED);
        return answerResponse;
    }
}

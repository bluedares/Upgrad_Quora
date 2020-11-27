package com.upgrad.quora.api.transformers;

import com.upgrad.quora.api.model.AnswerEditRequest;
import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Component;

@Component
public class AnswerEditRequestTransformer {

    /**
     *
     * @param answerEditRequest
     * @param answerUuid
     * @return
     * This method takes answer edit request object and answer id to be edited as parameters. Returns the answer entity object.
     */
    public AnswerEntity transform(AnswerEditRequest answerEditRequest,String answerUuid){
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(answerUuid);
        answerEntity.setAnswer(answerEditRequest.getContent());
        return answerEntity;
    }
}

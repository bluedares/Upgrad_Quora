package com.upgrad.quora.api.transformers;

import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Component;

@Component
public class AnswerRequestTransformer {
    /**
     *
     * @param answerRequest
     * @return
     * This method creates answer entity object for the given answer request object.
     */
    public AnswerEntity transform(AnswerRequest answerRequest){
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAnswer(answerRequest.getAnswer());
        return answerEntity;
    }
}

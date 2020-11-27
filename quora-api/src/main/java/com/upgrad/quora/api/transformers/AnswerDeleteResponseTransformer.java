package com.upgrad.quora.api.transformers;


import com.upgrad.quora.api.model.AnswerDeleteResponse;
import com.upgrad.quora.service.common.Constants;
import org.springframework.stereotype.Component;

@Component
public class AnswerDeleteResponseTransformer {
    /**
     *
     * @param answerUuid
     * @return AnswerDeleteResponse
     * This method creates answer delete response object with the answer id that is deleted and the status message.
     */
    public AnswerDeleteResponse transform(String answerUuid){
        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(answerUuid).status(Constants.ANSWER_DELETED);
        return answerDeleteResponse;
    }
}

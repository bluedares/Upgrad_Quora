package com.upgrad.quora.api.transformers;


import com.upgrad.quora.api.model.AnswerDeleteResponse;
import org.springframework.stereotype.Component;

@Component
public class AnswerDeleteResponseTransformer {
    public AnswerDeleteResponse transform(String answerUuid){
        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(answerUuid).status("");
        return answerDeleteResponse;
    }
}

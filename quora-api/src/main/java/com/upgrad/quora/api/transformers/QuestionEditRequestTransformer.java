package com.upgrad.quora.api.transformers;

import com.upgrad.quora.api.model.QuestionEditRequest;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Component;

@Component
public class QuestionEditRequestTransformer {
    public QuestionEntity transform(QuestionEditRequest questionEditRequest,String questionUuid){
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(questionUuid);
        questionEntity.setContent(questionEditRequest.getContent());
        return questionEntity;
    }
}

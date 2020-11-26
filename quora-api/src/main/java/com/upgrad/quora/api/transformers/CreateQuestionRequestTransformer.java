package com.upgrad.quora.api.transformers;

import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.UUID;

@Component
public class CreateQuestionRequestTransformer {
    public QuestionEntity transform(QuestionRequest questionRequest){
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setDate(ZonedDateTime.now());
        return questionEntity;
    }
}

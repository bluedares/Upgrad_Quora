package com.upgrad.quora.api.transformers;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionDetailsResponseTransformer {
    public List<QuestionDetailsResponse> transform(List<QuestionEntity> questionEntities){
        List<QuestionDetailsResponse> questionDetailsResponses = questionEntities.stream().map(questionEntity -> getQuestionDetailsResponse(questionEntity)).collect(Collectors.toList());
        return questionDetailsResponses;

    }
    private QuestionDetailsResponse getQuestionDetailsResponse(QuestionEntity questionEntity){
        QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse().id(questionEntity.getUuid()).content(questionEntity.getContent());
        return questionDetailsResponse;
    }
}

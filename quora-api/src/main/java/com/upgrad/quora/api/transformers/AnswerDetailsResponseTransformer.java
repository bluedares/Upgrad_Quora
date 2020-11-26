package com.upgrad.quora.api.transformers;

import com.upgrad.quora.api.model.AnswerDetailsResponse;
import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AnswerDetailsResponseTransformer {
    public List<AnswerDetailsResponse> transform(List<AnswerEntity> answerEntities){
        return answerEntities.stream().map(answerEntity -> createAnswerDetailsResponse(answerEntity)).collect(Collectors.toList());
    }

    private AnswerDetailsResponse createAnswerDetailsResponse(AnswerEntity answerEntity){
        AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse().id(answerEntity.getUuid()).
                answerContent(answerEntity.getAnswer()).questionContent(answerEntity.getQuestion().getContent());
        return answerDetailsResponse;
    }
}

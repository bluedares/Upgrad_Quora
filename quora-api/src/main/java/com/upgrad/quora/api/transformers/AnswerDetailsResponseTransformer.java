package com.upgrad.quora.api.transformers;

import com.upgrad.quora.api.model.AnswerDetailsResponse;
import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AnswerDetailsResponseTransformer {
    /**
     *
     * @param answerEntities
     * @return
     * This method converts list of answer entity objects to list of answer details response objects
     */
    public List<AnswerDetailsResponse> transform(List<AnswerEntity> answerEntities){
        return answerEntities.stream().map(answerEntity -> createAnswerDetailsResponse(answerEntity)).collect(Collectors.toList());
    }

    /**
     *
     * @param answerEntity
     * @return
     * This method creates answer details response object from answer entity object.
     */

    private AnswerDetailsResponse createAnswerDetailsResponse(AnswerEntity answerEntity){
        AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse().id(answerEntity.getUuid()).
                answerContent(answerEntity.getAnswer()).questionContent(answerEntity.getQuestion().getContent());
        return answerDetailsResponse;
    }
}

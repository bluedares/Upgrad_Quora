package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.api.transformers.CreateQuestionRequestTransformer;
import com.upgrad.quora.api.transformers.CreateQuestionResponseTransformer;
import com.upgrad.quora.api.transformers.QuestionDetailsResponseTransformer;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.QuestionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CreateQuestionRequestTransformer createQuestionRequestTransformer;

    @Autowired
    private CreateQuestionResponseTransformer createQuestionResponseTransformer;

    @Autowired
    private QuestionDetailsResponseTransformer questionDetailsResponseTransformer;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest, @RequestHeader("accessToken") final String accessToken) throws AuthorizationFailedException, AuthenticationFailedException {
        // Logic to handle Bearer <accesstoken>
        // User can give only Access token or Bearer <accesstoken> as input.
        String bearerToken = null;
        try {
            bearerToken = accessToken.split("Bearer ")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            bearerToken = accessToken;
        }
        QuestionEntity questionEntity = createQuestionRequestTransformer.transform(questionRequest);
        questionEntity = questionService.createQuestion(questionEntity,bearerToken);
        QuestionResponse questionResponse = createQuestionResponseTransformer.transform(questionEntity);
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }
    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("accessToken") final String accessToken) throws AuthenticationFailedException,AuthorizationFailedException, QuestionNotFoundException {
        // Logic to handle Bearer <accesstoken>
        // User can give only Access token or Bearer <accesstoken> as input.
        String bearerToken = null;
        try {
            bearerToken = accessToken.split("Bearer ")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            bearerToken = accessToken;
        }
        List<QuestionEntity> questionEntities = questionService.getAllQuestions(bearerToken);
        List<QuestionDetailsResponse> questionDetailsResponses = questionDetailsResponseTransformer.transform(questionEntities);
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponses,HttpStatus.OK);
    }
}

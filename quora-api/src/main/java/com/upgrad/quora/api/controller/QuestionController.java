package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.api.transformers.*;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.TokenService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CreateQuestionRequestTransformer createQuestionRequestTransformer;

    @Autowired
    private CreateQuestionResponseTransformer createQuestionResponseTransformer;

    @Autowired
    private QuestionDetailsResponseTransformer questionDetailsResponseTransformer;

    @Autowired
    private QuestionEditRequestTransformer questionEditRequestTransformer;

    @Autowired
    private QuestionEditResponseTransformer questionEditResponseTransformer;

    @Autowired
    private QuestionDeleteResponseTransformer questionDeleteResponseTransformer;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest, @RequestHeader("accessToken") final String accessToken) throws AuthorizationFailedException, AuthenticationFailedException {
        String bearerToken = tokenService.getBearerToken(accessToken);
        QuestionEntity questionEntity = createQuestionRequestTransformer.transform(questionRequest);
        questionEntity = questionService.createQuestion(questionEntity,bearerToken);
        QuestionResponse questionResponse = createQuestionResponseTransformer.transform(questionEntity);
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }
    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("accessToken") final String accessToken) throws AuthenticationFailedException,AuthorizationFailedException, QuestionNotFoundException {
        String bearerToken = tokenService.getBearerToken(accessToken);
        List<QuestionEntity> questionEntities = questionService.getAllQuestions(bearerToken);
        List<QuestionDetailsResponse> questionDetailsResponses = questionDetailsResponseTransformer.transform(questionEntities);
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponses,HttpStatus.OK);
    }
    @RequestMapping(method = RequestMethod.GET, path ="/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@PathVariable("userId") final String userId, @RequestHeader("accessToken") final String accessToken) throws QuestionNotFoundException,AuthenticationFailedException,AuthorizationFailedException, UserNotFoundException {
        String bearerToken = tokenService.getBearerToken(accessToken);
        List<QuestionEntity> questionEntities = questionService.getAllQuestionsByUser(userId,bearerToken);
        List<QuestionDetailsResponse> questionDetailsResponses = questionDetailsResponseTransformer.transform(questionEntities);
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponses,HttpStatus.OK);
    }
    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> updateQuestion(final QuestionEditRequest questionEditRequest, @PathVariable("questionId") final String questionId, @RequestHeader("accessToken") final String accessToken) throws AuthorizationFailedException, QuestionNotFoundException,AuthenticationFailedException {
        String bearerToken = tokenService.getBearerToken(accessToken);
        QuestionEntity questionEntity = questionEditRequestTransformer.transform(questionEditRequest,questionId);
        QuestionEntity updatedQuestionEntity = questionService.updateQuestion(questionEntity,bearerToken);
        QuestionEditResponse questionEditResponse = questionEditResponseTransformer.transform(updatedQuestionEntity);
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") final String questionId, @RequestHeader("accessToken") final String accessToken) throws AuthorizationFailedException, AuthenticationFailedException, QuestionNotFoundException {
        String bearerToken = tokenService.getBearerToken(accessToken);
        questionService.deleteQuestion(questionId,bearerToken);
        QuestionDeleteResponse questionDeleteResponse = questionDeleteResponseTransformer.transform(questionId);
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse,HttpStatus.OK);
    }
}

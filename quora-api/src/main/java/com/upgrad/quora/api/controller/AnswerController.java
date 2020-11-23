package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.api.transformers.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.TokenService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private AnswerRequestTransformer answerRequestTransformer;

    @Autowired
    private AnswerResponseTransformer answerResponseTransformer;

    @Autowired
    private AnswerEditRequestTransformer answerEditRequestTransformer;

    @Autowired
    private AnswerEditResponseTransformer answerEditResponseTransformer;

    @Autowired
    private AnswerDeleteResponseTransformer answerDeleteResponseTransformer;

    @RequestMapping(method = RequestMethod.POST,path = "/question/{questionId}/answer/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest,
                                                       @PathVariable("questionId") final String questionId, @RequestHeader("accessToken") final String accessToken)
            throws AuthorizationFailedException, QuestionNotFoundException, AuthenticationFailedException{
        String bearerToken = tokenService.getBearerToken(accessToken);
        AnswerEntity answerEntity = answerRequestTransformer.transform(answerRequest);
        answerEntity = answerService.createAnswer(answerEntity,questionId,bearerToken);
        AnswerResponse answerResponse = answerResponseTransformer.transform(answerEntity);
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(final AnswerEditRequest answerEditRequest,
                                                                @PathVariable("answerId") final String answerId, @RequestHeader("accessToken") final String accessToken)
            throws AuthorizationFailedException, AnswerNotFoundException, AuthenticationFailedException{
        String bearerToken = tokenService.getBearerToken(accessToken);
        AnswerEntity answerEntity = answerEditRequestTransformer.transform(answerEditRequest,answerId);
        answerEntity = answerService.updateAnswer(answerEntity,bearerToken);
        AnswerEditResponse answerEditResponse = answerEditResponseTransformer.transform(answerEntity);
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") final String answerId,
                                                             @RequestHeader("accessToken") final String accessToken) throws AuthenticationFailedException, AuthorizationFailedException, AnswerNotFoundException
    {
        String bearerToken = tokenService.getBearerToken(accessToken);
        answerService.deleteAnswer(answerId,bearerToken);
        AnswerDeleteResponse answerDeleteResponse = answerDeleteResponseTransformer.transform(answerId);
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse,HttpStatus.OK);
    }
}

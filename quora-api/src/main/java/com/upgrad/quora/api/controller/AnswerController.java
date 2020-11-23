package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.api.transformers.AnswerRequestTransformer;
import com.upgrad.quora.api.transformers.AnswerResponseTransformer;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.TokenService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.QuestionNotFoundException;
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

    @RequestMapping(method = RequestMethod.POST,path = "/question/{questionId}/answer/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest,
                                                       @PathVariable("questionId") final String questionId, @RequestHeader("accessToken") final String accessToken)
            throws AuthorizationFailedException, QuestionNotFoundException, AuthenticationFailedException{
        String bearerToken = tokenService.getBearerToken(accessToken);
        AnswerEntity answerEntity = answerRequestTransformer.transform(answerRequest);
        answerEntity = answerService.createAnswer(answerEntity,questionId,bearerToken);
        AnswerResponse answerResponse = answerResponseTransformer.transform(answerEntity);
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.OK);
    }
}

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

import java.util.List;

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

    @Autowired
    private AnswerDetailsResponseTransformer answerDetailsResponseTransformer;

    /**
     *
     * @param answerRequest
     * @param questionId
     * @param accessToken
     * @return
     * @throws AuthorizationFailedException
     * @throws QuestionNotFoundException
     * @throws AuthenticationFailedException
     * This api creates answer for the given question(question id). Answer content is passed via answerRequest object.
     * AnswerRequestTransformer creates answer entity object from AnswerRequest object.
     * Answer service business logic is called to create answer after validating user token and question.
     * AnswerResponseTransformer is called to create answer response object from the created answer entity object.
     * The above exceptions listed are thrown if any of user/question validations fails with customised messages.
     */
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

    /**
     *
     * @param answerEditRequest
     * @param answerId
     * @param accessToken
     * @return
     * @throws AuthorizationFailedException
     * @throws AnswerNotFoundException
     * @throws AuthenticationFailedException
     * This api updates the existing answer(answer id). Answer content to be updated is shared via AnswerEditRequest object.
     * AnswerEditRequestTransformer creates answer entity object from AnswerEditRequest object and answer id.
     * updateAnswer method of answer service is called to update the existing answer entity object.
     * AnswerEditResponseTransformer is used to create response object from the updated answer entity.
     * User access token is fetched to validate if the user who is modifying the answer is its owner.
     * Access token validations and Answer object validations are done before modifying the answer. If any of the validations fail, above exceptions are thrown with customised messages.
     */

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

    /**
     *
     * @param answerId
     * @param accessToken
     * @return
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws AnswerNotFoundException
     * This api deletes the existing answer with given answer id.
     * deleteAnswer method of answer service is called to delete the existing answer entity object.
     * AnswerDeleteResponseTransformer is used to create response object with the deleted answer id.
     * User Access token is validated if the user who is trying to delete the answer is the creator of the answer.
     * Access token validations and Answer object validations are done before modifying the answer. If any of the validations fail, above exceptions are thrown with customised messages.
     */

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

    /**
     *
     * @param questionId
     * @param accessToken
     * @return
     * @throws QuestionNotFoundException
     * @throws AnswerNotFoundException
     * @throws AuthorizationFailedException
     * @throws AuthenticationFailedException
     * This api retrieves all the answers to the given question.
     * getAllAnswersToQuestion method of answer service is called to fetch all answers for the given question.
     * Access Token and Question validations are done before fetching the answers. Above exceptions are thrown if any of the validations fail.
     */
    @RequestMapping(method = RequestMethod.GET, path = "/answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion (@PathVariable("questionId") final String questionId,
                                                                                @RequestHeader("accessToken") final String accessToken)
            throws QuestionNotFoundException, AnswerNotFoundException, AuthorizationFailedException, AuthenticationFailedException{
        String bearerToken = tokenService.getBearerToken(accessToken);
        List<AnswerEntity> answerEntities = answerService.getAllAnswersToQuestion(questionId,bearerToken);
        List<AnswerDetailsResponse> answerDetailsResponses = answerDetailsResponseTransformer.transform(answerEntities);
        return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponses,HttpStatus.OK);
    }
}

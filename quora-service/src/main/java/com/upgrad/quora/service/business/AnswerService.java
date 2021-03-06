package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.QuoraErrors;
import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.QuestionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.sql.rowset.spi.TransactionalWriter;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * This is a service class that deals with answer related CRUD operations
 */
@Service
public class AnswerService {
    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;

    /**
     *
     * @param answerEntity
     * @param questionId
     * @param accessToken
     * @return
     * @throws QuestionNotFoundException
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * This method creates answer for the given question. This validates the user access token using validateAccessToken method.
     * Post user validation, we validate if the question exists. If any of those validations fail, the above exceptions are thrown.
     * Else we proceed to create the answer.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(AnswerEntity answerEntity,String questionId, String accessToken) throws QuestionNotFoundException, AuthenticationFailedException, AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userService.validateAccessToken(accessToken);
        QuestionEntity existingQuestion = questionDao.getQuestionByQuestionUuid(questionId);
        if(existingQuestion == null){
            throw new QuestionNotFoundException(QuoraErrors.QUESTION_DOES_NOT_EXIST);
        }
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setDate(ZonedDateTime.now());
        answerEntity.setUser(userAuthEntity.getUser());
        answerEntity.setQuestion(existingQuestion);
        return answerDao.createAnswer(answerEntity);
    }

    /**
     *
     * @param existingAnswer
     * @param userAuthEntity
     * @throws AuthorizationFailedException
     * @throws AnswerNotFoundException
     * This method is called while user tries to edit/delete the answer.
     * This method validates the answer id. We check whether the answer exists.
     * We also validate whether user who is trying to edit/delete the answer is the one who actually answered it(i.e.)the owner of the answer.
     * If the answer does not exists or if the user is not the answer owner, then we throw the above exceptions.
     */
    private void validateAnswer(AnswerEntity existingAnswer,UserAuthEntity userAuthEntity) throws AuthorizationFailedException,AnswerNotFoundException{
        if(existingAnswer == null){
            throw new AnswerNotFoundException(QuoraErrors.ANSWER_DOES_NOT_EXIST);
        }
        UserEntity answerOwner = existingAnswer.getUser();
        UserEntity currentUser = userAuthEntity.getUser();
        if(currentUser.getId() != answerOwner.getId()){
            throw new AuthorizationFailedException(QuoraErrors.ANSWER_NON_OWNER);
        }
    }

    /**
     *
     * @param answerEntity
     * @param accessToken
     * @return
     * @throws AnswerNotFoundException
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * This method is used to modify the existing answer. We validate the user by calling validateAccessToken method.
     * We also validate if answer the exists and if user is authorized to update the answer.This is done by validateAnswer method.
     * If any of the validations fail, above exceptions are thrown.
     * Else we proceed to modify the answer.
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity updateAnswer(AnswerEntity answerEntity, String accessToken) throws AnswerNotFoundException,AuthenticationFailedException,AuthorizationFailedException{
        UserAuthEntity userAuthEntity = userService.validateAccessToken(accessToken);
        AnswerEntity existingAnswer = answerDao.getAnswerByUuid(answerEntity.getUuid());
        validateAnswer(existingAnswer,userAuthEntity);
        answerEntity.setQuestion(existingAnswer.getQuestion());
        answerEntity.setDate(existingAnswer.getDate());
        answerEntity.setId(existingAnswer.getId());
        answerEntity.setUser(existingAnswer.getUser());
        return answerDao.updateAnswer(answerEntity);
    }

    /**
     *
     * @param answerUuid
     * @param accessToken
     * @throws AnswerNotFoundException
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * This method is used to delete the existing answer. We validate the user by calling validateAccessToken method.
     * We also validate if answer the exists and if user is authorized to delete the answer.This is done by validateAnswer method.
     * If any of the validations fail, above exceptions are thrown.
     * Else we proceed to delete the answer.
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAnswer(String answerUuid,String accessToken) throws AnswerNotFoundException, AuthenticationFailedException, AuthorizationFailedException{
        UserAuthEntity userAuthEntity = userService.validateAccessToken(accessToken);
        AnswerEntity existingAnswer = answerDao.getAnswerByUuid(answerUuid);
        validateAnswer(existingAnswer,userAuthEntity);
        answerDao.deleteAnswer(existingAnswer);
    }

    /**
     *
     * @param questionUuid
     * @param accessToken
     * @return
     * @throws AnswerNotFoundException
     * @throws QuestionNotFoundException
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * This method is used to fetch all the answers to the given question. We validate the user by calling validateAccessToken method.
     * We validate if the question exists. If any of the validations fail, we throw above exceptions.
     * Else we proceed to fetch all the answers to the question from our DB. If the answers list is empty, we throw exception indicating
     * there are no answers to the given question.
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public List<AnswerEntity> getAllAnswersToQuestion(String questionUuid,String accessToken) throws AnswerNotFoundException, QuestionNotFoundException, AuthenticationFailedException, AuthorizationFailedException{
        UserAuthEntity userAuthEntity = userService.validateAccessToken(accessToken);
        QuestionEntity questionEntity = questionDao.getQuestionByQuestionUuid(questionUuid);
        if(questionEntity == null){
            throw new QuestionNotFoundException(QuoraErrors.QUESTION_DOES_NOT_EXIST);
        }
        List<AnswerEntity> answerEntities = answerDao.getAllAnswersToQuestion(questionUuid);
        if(CollectionUtils.isEmpty(answerEntities)){
            throw new AnswerNotFoundException(QuoraErrors.NO_ANSWERS_TO_QUESTION);
        }
        return answerEntities;
    }
}

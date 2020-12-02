package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.LoginStatus;
import com.upgrad.quora.service.common.QuoraErrors;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.swing.plaf.ProgressBarUI;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserService userService;

    /**
     *
     * @param questionEntity
     * @param accessToken
     * @return
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * User access token is validated.
     * If validation fails, one of the above exceptions are thrown.
     * Else we create the question and map the current user to it.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity, String accessToken) throws AuthenticationFailedException,AuthorizationFailedException{
        UserAuthEntity userAuthEntity = userService.validateAccessToken(accessToken);
        questionEntity.setUser(userAuthEntity.getUser());
        return questionDao.createQuestion(questionEntity);
    }

    /**
     *
     * @param accessToken
     * @return
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws QuestionNotFoundException
     * User Access Token is validated. If any of the validations fail, one of the above excpetions are thrown.
     * We fetch all the questions from DB. If there are no questions present in the DB, we return the appropriate error messsage indicating the same.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestions(String accessToken) throws AuthenticationFailedException,AuthorizationFailedException,QuestionNotFoundException{
        UserAuthEntity userAuthEntity = userService.validateAccessToken(accessToken);
        List<QuestionEntity> questionEntities = questionDao.getAllQuestions();
        if(CollectionUtils.isEmpty(questionEntities)){
            throw new QuestionNotFoundException(QuoraErrors.NO_QUESTIONS_PRESENT);
        }
        return questionEntities;
    }

    /**
     *
     * @param userUuid
     * @param accessToken
     * @return
     * @throws QuestionNotFoundException
     * @throws AuthorizationFailedException
     * @throws AuthenticationFailedException
     * @throws UserNotFoundException
     * User Access Token is validated. If any of the validations fail, one of the above excpetions are thrown.
     *  We fetch all the questions asked by the given user from DB. If there are no questions present in the DB, we return the appropriate error messsage indicating the same.
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestionsByUser(String userUuid,String accessToken) throws QuestionNotFoundException,AuthorizationFailedException, AuthenticationFailedException, UserNotFoundException {
        UserEntity userEntity = userService.getUser(userUuid,accessToken);
        List<QuestionEntity> questionEntities = questionDao.getAllQuestionsByUser(userEntity.getUuid());
        if(CollectionUtils.isEmpty(questionEntities)){
            throw new QuestionNotFoundException(QuoraErrors.NO_QUESTIONS_BY_USER);
        }
        return questionEntities;
    }

    /**
     *
     * @param questionEntity
     * @param accessToken
     * @return
     * @throws QuestionNotFoundException
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * User Access Token and the given question are validated.
     * If any of the validations (user sign in/token validations, question existence/ownership) fail, one of the above exceptions are thrown.
     * Else we proceed to let the user to update the question.
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity updateQuestion(QuestionEntity questionEntity, String accessToken) throws QuestionNotFoundException, AuthenticationFailedException,AuthorizationFailedException{
        UserAuthEntity userAuthEntity = userService.validateAccessToken(accessToken);
        QuestionEntity existingQuestion = questionDao.getQuestionByQuestionUuid(questionEntity.getUuid());
        validateQuestion(userAuthEntity,existingQuestion);
        questionEntity.setId(existingQuestion.getId());
        questionEntity.setDate(existingQuestion.getDate());
        questionEntity.setUser(existingQuestion.getUser());
        return questionDao.updateQuestion(questionEntity);
    }

    /**
     *
     * @param questionId
     * @param accessToken
     * @throws QuestionNotFoundException
     * @throws AuthorizationFailedException
     * @throws AuthenticationFailedException
     * User Access Token and the given question are validated.
     * If any of the validations (user sign in/token validations, question existence/ownership) fail, one of the above exceptions are thrown.
     * Else we proceed to let the user to delete the question.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteQuestion(String questionId, String accessToken) throws QuestionNotFoundException,AuthorizationFailedException,AuthenticationFailedException{
        UserAuthEntity userAuthEntity = userService.validateAccessToken(accessToken);
        QuestionEntity existingQuestion = questionDao.getQuestionByQuestionUuid(questionId);
        validateQuestion(userAuthEntity,existingQuestion);
        questionDao.deleteQuestion(existingQuestion);
    }

    /**
     *
     * @param userAuthEntity
     * @param existingQuestion
     * @throws QuestionNotFoundException
     * @throws AuthorizationFailedException
     * This method validates whether question exists. We also check whether the logged in user who is trying to modify/delete the question is the owner of the question.
     * If any of the validations fail, we throw one of the above exceptions with appropriate error message.
     */
    private void validateQuestion(UserAuthEntity userAuthEntity, QuestionEntity existingQuestion) throws QuestionNotFoundException, AuthorizationFailedException{
        if(existingQuestion == null){
            throw new QuestionNotFoundException(QuoraErrors.QUESTION_DOES_NOT_EXIST);
        }
        UserEntity owner = existingQuestion.getUser();
        UserEntity currentUser = userAuthEntity.getUser();
        if(owner.getId() != currentUser.getId()){
            throw new AuthorizationFailedException(QuoraErrors.QUESTION_NON_OWNER);
        }
    }

}

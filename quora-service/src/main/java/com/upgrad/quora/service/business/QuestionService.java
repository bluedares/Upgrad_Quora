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

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity, String accessToken) throws AuthenticationFailedException,AuthorizationFailedException{
        UserAuthEntity userAuthEntity = userService.validateAccessToken(accessToken);
        questionEntity.setUser(userAuthEntity.getUser());
        return questionDao.createQuestion(questionEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestions(String accessToken) throws AuthenticationFailedException,AuthorizationFailedException,QuestionNotFoundException{
        UserAuthEntity userAuthEntity = userService.validateAccessToken(accessToken);
        List<QuestionEntity> questionEntities = questionDao.getAllQuestions();
        if(CollectionUtils.isEmpty(questionEntities)){
            throw new QuestionNotFoundException(QuoraErrors.NO_QUESTIONS_PRESENT);
        }
        return questionEntities;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestionsByUser(String userUuid,String accessToken) throws QuestionNotFoundException,AuthorizationFailedException, AuthenticationFailedException, UserNotFoundException {
        UserEntity userEntity = userService.getUser(userUuid,accessToken);
        List<QuestionEntity> questionEntities = questionDao.getAllQuestionsByUser(userEntity.getUuid());
        if(CollectionUtils.isEmpty(questionEntities)){
            throw new QuestionNotFoundException(QuoraErrors.NO_QUESTIONS_BY_USER);
        }
        return questionEntities;
    }

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

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteQuestion(String questionId, String accessToken) throws QuestionNotFoundException,AuthorizationFailedException,AuthenticationFailedException{
        UserAuthEntity userAuthEntity = userService.validateAccessToken(accessToken);
        QuestionEntity existingQuestion = questionDao.getQuestionByQuestionUuid(questionId);
        validateQuestion(userAuthEntity,existingQuestion);
        questionDao.deleteQuestion(existingQuestion);
    }

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

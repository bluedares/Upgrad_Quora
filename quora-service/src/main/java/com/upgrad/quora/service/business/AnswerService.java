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

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAnswer(String answerUuid,String accessToken) throws AnswerNotFoundException, AuthenticationFailedException, AuthorizationFailedException{
        UserAuthEntity userAuthEntity = userService.validateAccessToken(accessToken);
        AnswerEntity existingAnswer = answerDao.getAnswerByUuid(answerUuid);
        validateAnswer(existingAnswer,userAuthEntity);
        answerDao.deleteAnswer(existingAnswer);
    }

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

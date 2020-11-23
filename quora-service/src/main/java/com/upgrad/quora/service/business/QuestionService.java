package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.LoginStatus;
import com.upgrad.quora.service.common.QuoraErrors;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.QuestionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
}

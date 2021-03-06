package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * This is a repository class that deals with Answer related CRUD operations.
 */
@Repository
public class AnswerDao {
    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity){
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public AnswerEntity updateAnswer(AnswerEntity answerEntity){
        entityManager.merge(answerEntity);
        return answerEntity;
    }

    public void deleteAnswer(AnswerEntity answerEntity){
        entityManager.remove(answerEntity);
    }

    public AnswerEntity getAnswerByUuid(String answerUuid){
        try{
            return entityManager.createNamedQuery("answerEntityByUuid",AnswerEntity.class).setParameter("uuid",answerUuid).getSingleResult();
        }
        catch (NoResultException nre){
            return null;
        }
    }

    public List<AnswerEntity> getAllAnswersToQuestion(String questionUuid){
        try{
            return entityManager.createNamedQuery("answersByQuestionId",AnswerEntity.class).setParameter("uuid",questionUuid).getResultList();
        }
        catch(NoResultException nre){
            return null;
        }
    }
}

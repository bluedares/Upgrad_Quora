package com.upgrad.quora.service.dao;


import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity){
        entityManager.persist(userEntity);
        return userEntity;
    }

    public UserAuthEntity createUserAuth(UserAuthEntity userAuthEntity){
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }

    public UserEntity getUserByEmail(String email){
        try{
            return entityManager.createNamedQuery("userByEmail",UserEntity.class).setParameter("email",email).getSingleResult();
        }
        catch(NoResultException nre){
            return null;
        }
    }

    public UserEntity getUserByUuid(String uuid){
        try{
            return entityManager.createNamedQuery("userByUuid",UserEntity.class).setParameter("uuid",uuid).getSingleResult();
        }
        catch (NoResultException nre){
            return null;
        }
    }

    public UserEntity getUserByUserName(String userName){
        try{
            return entityManager.createNamedQuery("userByUserName",UserEntity.class).setParameter("userName",userName).getSingleResult();
        }
        catch(NoResultException nre){
            return null;
        }
    }

    public UserAuthEntity getUserAuthByAccessToken(String accessToken){
        try{
            return entityManager.createNamedQuery("userAuthTokenByAccessToken",UserAuthEntity.class).setParameter("accessToken",accessToken).getSingleResult();
        }
        catch (NoResultException nre){
            return null;
        }
    }

    public UserAuthEntity logoutUser(UserAuthEntity userAuthEntity){
        entityManager.merge(userAuthEntity);
        return userAuthEntity;
    }

    public void deleteUser(UserEntity userEntity){
        entityManager.remove(userEntity);
    }
}

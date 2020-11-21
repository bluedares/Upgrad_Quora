package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.UserRole;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;


    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException{
        userEntity.setRole(UserRole.NON_ADMIN);
        return createUser(userEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity createUser(UserEntity userEntity) throws SignUpRestrictedException{
        if(isUserEmailExist(userEntity)){
            throw new SignUpRestrictedException("SUP-001","This user has already registered. Please register through another email");
        }

        if(isUserNameExist(userEntity)){
            throw new SignUpRestrictedException("SUP-002","This user name has already been chosen. Please choose another user name");
        }
        if(userEntity.getRole() == null){
            userEntity.setRole(UserRole.ADMIN);
        }
        return userDao.createUser(userEntity);
    }

    private boolean isUserNameExist(UserEntity userEntity){
        UserEntity existingUser = userDao.getUserByUserName(userEntity.getUserName());
        return existingUser == null ? false : true;
    }

    private boolean isUserEmailExist(UserEntity userEntity){
        UserEntity existingUser = userDao.getUserByEmail(userEntity.getEmail());
        return existingUser == null ? false : true;
    }
}

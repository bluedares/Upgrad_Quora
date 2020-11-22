package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.LoginStatus;
import com.upgrad.quora.service.common.QuoraErrors;
import com.upgrad.quora.service.common.UserRole;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;


    /**
     *
     * @param userEntity
     * @return userEntity
     * This method is used to create a non-admin user
     * @throws SignUpRestrictedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException{
        userEntity.setRole(UserRole.NON_ADMIN.name());
        return createUser(userEntity);
    }

    /**
     *
     * @param userEntity
     * @return
     * This method is used to create both admin and non admin users. If role is not set in prior, the user role is assumed to be admin.
     * Validate the existence of chosen user name and email. If exists, throw exception. Else create the user.
     * @throws SignUpRestrictedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity createUser(UserEntity userEntity) throws SignUpRestrictedException{
        if(isUserEmailExist(userEntity)){
            throw new SignUpRestrictedException(QuoraErrors.USER_EMAIL_ALREADY_EXISTS);
        }

        if(isUserNameExist(userEntity)){
            throw new SignUpRestrictedException(QuoraErrors.USER_NAME_ALREADY_EXISTS);
        }
        if(userEntity.getRole() == null){
            userEntity.setRole(UserRole.ADMIN.name());
        }
        String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        userEntity.setLoginStatus(LoginStatus.REGISTERED.name());
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

    public UserAuthEntity authenticate(String userName, String password) throws AuthenticationFailedException{
        UserEntity userEntity = userDao.getUserByUserName(userName);
        if(userEntity == null){
            throw new AuthenticationFailedException(QuoraErrors.USER_NOT_FOUND);
        }
        String encryptedPassword = passwordCryptographyProvider.encrypt(password,userEntity.getSalt());
        if(!encryptedPassword.equals(userEntity.getPassword())){
            throw new AuthenticationFailedException(QuoraErrors.INCORRECT_PASSWORD);
        }
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
        UserAuthEntity userAuthEntity = new UserAuthEntity();
        ZonedDateTime currentTime = ZonedDateTime.now();
        ZonedDateTime expiresAt = currentTime.plusHours(8);
        userAuthEntity.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(),currentTime,expiresAt));
        userAuthEntity.setLoginAt(currentTime);
        userAuthEntity.setExpiresAt(expiresAt);
        userAuthEntity.setUuid(userEntity.getUuid());
        userEntity.setLoginStatus(LoginStatus.LOGGED_IN.name());
        userAuthEntity.setUser(userEntity);
        return userDao.createUserAuth(userAuthEntity);
    }
}

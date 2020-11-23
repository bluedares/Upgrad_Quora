package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.LoginStatus;
import com.upgrad.quora.service.common.QuoraErrors;
import com.upgrad.quora.service.common.UserRole;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
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

    @Autowired
    private TokenService tokenService;


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

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity authenticate(String userName, String password) throws AuthenticationFailedException{
        UserEntity userEntity = userDao.getUserByUserName(userName);
        if(userEntity == null){
            throw new AuthenticationFailedException(QuoraErrors.USER_NOT_REGISTERED);
        }
        String encryptedPassword = passwordCryptographyProvider.encrypt(password,userEntity.getSalt());
        if(!encryptedPassword.equals(userEntity.getPassword())){
            throw new AuthenticationFailedException(QuoraErrors.INCORRECT_PASSWORD);
        }
        UserAuthEntity userAuthEntity = userDao.getUserAuthByUuid(userEntity.getUuid());
        if(userAuthEntity == null){
            userAuthEntity = new UserAuthEntity();
        }
        ZonedDateTime currentTime = ZonedDateTime.now();
        ZonedDateTime expiresAt = currentTime.plusHours(8);
        userAuthEntity.setAccessToken(tokenService.generateToken(encryptedPassword,userEntity.getUuid(),currentTime,expiresAt));
        userAuthEntity.setLoginAt(currentTime);
        userAuthEntity.setExpiresAt(expiresAt);
        userAuthEntity.setUuid(userEntity.getUuid());
        userEntity.setLoginStatus(LoginStatus.LOGGED_IN.name());
        userAuthEntity.setUser(userEntity);
        userAuthEntity = userDao.createUserAuth(userAuthEntity);
        return userAuthEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity logout(String accessToken) throws SignOutRestrictedException{
        UserAuthEntity userAuthEntity = userDao.getUserAuthByAccessToken(accessToken);
        if(userAuthEntity == null){
            throw new SignOutRestrictedException(QuoraErrors.INVALID_ACCESS_TOKEN);
        }
        UserEntity userEntity = userAuthEntity.getUser();
        if(!LoginStatus.LOGGED_IN.equals(LoginStatus.valueOf(userEntity.getLoginStatus()))){
            throw new SignOutRestrictedException(QuoraErrors.USER_NOT_SIGNED_IN);
        }
        userAuthEntity.setLogoutAt(ZonedDateTime.now());
        userEntity.setLoginStatus(LoginStatus.LOGGED_OUT.name());
        userAuthEntity.setUser(userEntity);
        return userDao.logoutUser(userAuthEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUser(String uuid,String accessToken) throws AuthenticationFailedException ,AuthorizationFailedException, UserNotFoundException{
        UserAuthEntity userAuthEntity = validateAccessToken(accessToken);
        UserEntity userEntity = userDao.getUserByUuid(uuid);
        if(userEntity == null){
            throw new UserNotFoundException(QuoraErrors.USER_NOT_FOUND);
        }
        return userEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteUser(String uuid,String accessToken) throws AuthenticationFailedException,AuthorizationFailedException, UserNotFoundException{
        UserAuthEntity userAuthEntity = validateAccessToken(accessToken);
        if(!UserRole.ADMIN.equals(UserRole.valueOf(userAuthEntity.getUser().getRole()))){
            throw new AuthorizationFailedException(QuoraErrors.NOT_AN_ADMIN);
        }
        UserEntity userEntity = userDao.getUserByUuid(uuid);
        if(userEntity == null){
            throw new UserNotFoundException(QuoraErrors.USER_TO_BE_DELETED_DOES_NOT_EXIST);
        }
        userDao.deleteUser(userEntity);
        return uuid;
    }
    public UserAuthEntity validateAccessToken(String accessToken) throws AuthenticationFailedException,AuthorizationFailedException{
        UserAuthEntity userAuthEntity = userDao.getUserAuthByAccessToken(accessToken);
        if(userAuthEntity == null){
            throw new AuthorizationFailedException(QuoraErrors.INVALID_ACCESS_TOKEN);
        }
        if(ZonedDateTime.now().isAfter(userAuthEntity.getExpiresAt())){
            throw new AuthorizationFailedException(QuoraErrors.EXPIRED_ACCESS_TOKEN);
        }
        if(!LoginStatus.LOGGED_IN.equals(LoginStatus.valueOf(userAuthEntity.getUser().getLoginStatus()))){
            throw new AuthenticationFailedException(QuoraErrors.USER_NOT_SIGNED_IN);
        }
        return userAuthEntity;
    }
}

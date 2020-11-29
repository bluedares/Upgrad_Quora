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

    /**
     *
     * @param userEntity
     * @return
     * This method checks whether the user name in temporary userEntity object (received as param) exist in the DB.
     * Returns true if the username  exists else false.
     */
    private boolean isUserNameExist(UserEntity userEntity){
        UserEntity existingUser = userDao.getUserByUserName(userEntity.getUserName());
        return existingUser == null ? false : true;
    }

    /**
     *
     * @param userEntity
     * @return
     * This method checks whether email in temporary userEntity object (received as param) exist in the DB.
     * Returns true if the email exists else false.
     */
    private boolean isUserEmailExist(UserEntity userEntity){
        UserEntity existingUser = userDao.getUserByEmail(userEntity.getEmail());
        return existingUser == null ? false : true;
    }

    /**
     *
     * @param userName
     * @param password
     * @return
     * @throws AuthenticationFailedException
     * This method checks if the username exist in the Database. If not exists, throw the above exception with error message indicating the user has not registered.
     * Post validating the existence of username, we encrypt the given password using the existing salt in the DB and compare it with the encrypted password saved in the DB.
     * If both passwords are not equal, we throw the above exception with message indicating that password is incorrect.
     * Post validating the password, we fetch the existing user details.
     * We also fetch the user authentication details using user uuid.
     * If user has already signed in before, we update the access token and validation duration.
     * Else we create new user authentication details if user is signing in for the first time.
     */
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
        Boolean hasUserLoggedInBefore = userAuthEntity == null ? false:true;
        if(!hasUserLoggedInBefore){
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
        userAuthEntity = hasUserLoggedInBefore? userDao.updateUserAuth(userAuthEntity) : userDao.createUserAuth(userAuthEntity);
        return userAuthEntity;
    }

    /**
     *
     * @param accessToken
     * @return
     * @throws SignOutRestrictedException
     * This method logs out the user. First we validate whether if the user access token is valid. We check if the user is currently logged in.
     * If any of the above validations fail, we throw the above exception with the appropriate error message.
     */
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

    /**
     *
     * @param uuid
     * @param accessToken
     * @return
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     * This method is used to fetch the user details based on the given user id. We first validate the access token. If the validation fails, one of the above exceptions are thrown.
     * If validations are passed, user details are fetched based on user id.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUser(String uuid,String accessToken) throws AuthenticationFailedException ,AuthorizationFailedException, UserNotFoundException{
        UserAuthEntity userAuthEntity = validateAccessToken(accessToken);
        UserEntity userEntity = userDao.getUserByUuid(uuid);
        if(userEntity == null){
            throw new UserNotFoundException(QuoraErrors.USER_NOT_FOUND);
        }
        return userEntity;
    }

    /**
     *
     * @param uuid
     * @param accessToken
     * @return
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     * This method is used to delete the user details based on the given user id.
     * Access token validations are performed. If any of the validations fail, above exceptions are thrown
     * If the current user is not an admin, then throw the exception indicating the same.
     * Delete the user based on id only if the current user is an admin.
     */
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

    /**
     *
     * @param accessToken
     * @return
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * This method validates the user access token. If the user access token is not present in the DB, then throw above exception with messsage indicating that the token is invalid.
     * Check if the current time is past token expired at time. If so, throw exception indicating that the token is expired,
     * Check if the user has currently signed. If not, throw exception indicating that the user has not signed in.
     * Return the user auth details post the validations are passed.
     */
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

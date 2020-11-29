package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.api.transformers.*;
import com.upgrad.quora.service.business.TokenService;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.common.Constants;
import com.upgrad.quora.service.common.InputUserCredentials;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;


/**
 * This is a controller class that deals with User related CRUD operations and authenticate user.
 */
@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SignupUserRequestTransformer signupUserRequestTransformer;

    @Autowired
    private SignupUserResponseTransformer signupUserResponseTransformer;

    @Autowired
    private SigninResponseTransformer signinResponseTransformer;

    @Autowired
    private SignoutResponseTransformer signoutResponseTransformer;

    @Autowired
    private UserDetailsResponseTransformer userDetailsResponseTransformer;

    @Autowired
    private UserDeleteResponseTransformer userDeleteResponseTransformer;
    /**
     *
     * @param signupUserRequest : To create a non admin user with his specific details.
     * @return SignupUserResponse: Returns ID and login status of the user.
     * @throws SignUpRestrictedException
     */

    @RequestMapping(method = RequestMethod.POST, path = "/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> userSignup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        UserEntity userEntity = signupUserRequestTransformer.transform(signupUserRequest);
        userEntity = userService.signup(userEntity);
        SignupUserResponse signupUserResponse = signupUserResponseTransformer.transform(userEntity);
        return new ResponseEntity<SignupUserResponse>(signupUserResponse,HttpStatus.CREATED);
    }

    /**
     *
     * @param signupUserRequest : To create an admin user with his specific details.
     * @return SignupUserResponse: Returns ID and login status of the user.
     * @throws SignUpRestrictedException
     */

    @RequestMapping(method = RequestMethod.POST, path = "/user/admin/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> adminSignup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        UserEntity userEntity = signupUserRequestTransformer.transform(signupUserRequest);
        userEntity = userService.createUser(userEntity);
        SignupUserResponse signupUserResponse = signupUserResponseTransformer.transform(userEntity);
        return new ResponseEntity<SignupUserResponse>(signupUserResponse,HttpStatus.CREATED);
    }

    /**
     *
     * @param authorization
     * @return
     * @throws AuthenticationFailedException
     * This api receives authorization string as input. It decodes the user credentials by calling the corresponding method in Token service.
     * We authenticate the user by calling authenticate method in userService.
     * SigninResponseTransform creates the response object from the fetched userEntity object.
     * Above exception is thrown if credentials are invalid.
     */
    @RequestMapping(method = RequestMethod.POST , path = "/user/signin",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> authenticate(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException{
        InputUserCredentials inputUserCredentials = tokenService.getUserCredentialsFromBasicToken(authorization);
        UserAuthEntity userAuthEntity = userService.authenticate(inputUserCredentials.getInputUserName(),inputUserCredentials.getInputPassword());
        UserEntity userEntity = userAuthEntity.getUser();
        SigninResponse signinResponse = signinResponseTransformer.transform(userEntity);
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constants.ACCESS_TOKEN,userAuthEntity.getAccessToken());
        return new ResponseEntity<SigninResponse>(signinResponse,headers,HttpStatus.OK);
    }

    /**
     *
     * @param accessToken
     * @return
     * @throws SignOutRestrictedException
     * This api logs out the user from the application. We receive user access token as input and validate the user credentials.
     * If validations fail, above exception is thrown. Else we log out the user.
     * SignoutResponseTransformer creates the response object from the
     */
    @RequestMapping(method = RequestMethod.POST , path = "/user/signout",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> logout(@RequestHeader("accessToken") final String accessToken) throws SignOutRestrictedException{
        String bearerToken = tokenService.getBearerToken(accessToken);
        UserAuthEntity userAuthEntity = userService.logout(bearerToken);
        SignoutResponse signoutResponse = signoutResponseTransformer.transform(userAuthEntity);
        return new ResponseEntity<SignoutResponse>(signoutResponse,HttpStatus.OK);
    }

    /**
     *
     * @param userUuId
     * @param accessToken
     * @return
     * @throws AuthenticationFailedException
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     * This api fetches the user details for the given id. We validate the access token and whether the user id exists.
     * If any of the validations fail, the above exceptions are thrown. Else user details are fetched.
     * UserDetailsResponseTransformer creates a response object from the existing user entity object.
     */
    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUser(@PathVariable("userId")String userUuId, @RequestHeader("accessToken") final String accessToken) throws AuthenticationFailedException, AuthorizationFailedException,UserNotFoundException{
        String bearerToken = tokenService.getBearerToken(accessToken);
        UserEntity userEntity = userService.getUser(userUuId,bearerToken);
        UserDetailsResponse userDetailsResponse = userDetailsResponseTransformer.transform(userEntity);
        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse,HttpStatus.OK);
    }

    /**
     *
     * @param userUuid
     * @param accessToken
     * @return
     * @throws AuthorizationFailedException
     * @throws AuthenticationFailedException
     * @throws UserNotFoundException
     * This api deletes the user corresponding to the given user id. Access token validations and user id validations are performed.
     * Current user's role is also checked whether he is an admin. If any of these validations fail, the above exceptions are thrown.
     * Else we delete the user. UserDeleteResponseTransformer creates a response object for the deleted user id.
     */

    @RequestMapping(method = RequestMethod.DELETE, path = "/admin/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> deleteUser(@PathVariable("userId")String userUuid,@RequestHeader("accessToken")final String accessToken) throws AuthorizationFailedException,AuthenticationFailedException,UserNotFoundException{
        String bearerToken = tokenService.getBearerToken(accessToken);
        String uuid = userService.deleteUser(userUuid,bearerToken);
        UserDeleteResponse userDeleteResponse = userDeleteResponseTransformer.transform(uuid);
        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse,HttpStatus.OK);
    }
}

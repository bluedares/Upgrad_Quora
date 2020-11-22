package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.api.transformers.*;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.common.Constants;
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


@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService;

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

    @RequestMapping(method = RequestMethod.POST , path = "/user/signin",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> authenticate(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException{
        String[] authorizationArr = authorization.split("Basic ");
        byte[] decode = Base64.getDecoder().decode(authorizationArr[1]);
        String decodedString = new String(decode);
        String[] authArr = decodedString.split(":");
        UserAuthEntity userAuthEntity = userService.authenticate(authArr[0],authArr[1]);
        UserEntity userEntity = userAuthEntity.getUser();
        SigninResponse signinResponse = signinResponseTransformer.transform(userEntity);
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constants.ACCESS_TOKEN,userAuthEntity.getAccessToken());
        return new ResponseEntity<SigninResponse>(signinResponse,headers,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST , path = "/user/signout",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> logout(@RequestHeader("accessToken") final String accessToken) throws SignOutRestrictedException{
        String bearerToken = null;
        try {
            String[] accessTokenArr = accessToken.split("Bearer ");
            bearerToken = accessTokenArr[1];
        }
        catch (ArrayIndexOutOfBoundsException exe){
            bearerToken = accessToken;
        }

        UserAuthEntity userAuthEntity = userService.logout(bearerToken);
        SignoutResponse signoutResponse = signoutResponseTransformer.transform(userAuthEntity);
        return new ResponseEntity<SignoutResponse>(signoutResponse,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUser(@PathVariable("userId")String userUuId, @RequestHeader("accessToken") final String accessToken) throws AuthenticationFailedException, AuthorizationFailedException,UserNotFoundException{
        String bearerToken = null;
        try {
            String[] accessTokenArr = accessToken.split("Bearer ");
            bearerToken = accessTokenArr[1];
        }
        catch (ArrayIndexOutOfBoundsException exe){
            bearerToken = accessToken;
        }

        UserEntity userEntity = userService.getUser(userUuId,bearerToken);
        UserDetailsResponse userDetailsResponse = userDetailsResponseTransformer.transform(userEntity);
        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/admin/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> deleteUser(@PathVariable("userId")String userUuid,@RequestHeader("accessToken")final String accessToken) throws AuthorizationFailedException,AuthenticationFailedException,UserNotFoundException{
        String bearerToken = null;
        try {
            String[] accessTokenArr = accessToken.split("Bearer ");
            bearerToken = accessTokenArr[1];
        }
        catch (ArrayIndexOutOfBoundsException exe){
            bearerToken = accessToken;
        }
        String uuid = userService.deleteUser(userUuid,bearerToken);
        UserDeleteResponse userDeleteResponse = userDeleteResponseTransformer.transform(uuid);
        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse,HttpStatus.OK);
    }
}

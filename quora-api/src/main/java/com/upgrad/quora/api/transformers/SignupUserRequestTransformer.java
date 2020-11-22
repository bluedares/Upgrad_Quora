package com.upgrad.quora.api.transformers;

import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SignupUserRequestTransformer {
    public UserEntity transform(SignupUserRequest signupUserRequest){
        UserEntity userEntity = new UserEntity();
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setContactNumber(signupUserRequest.getContactNumber());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        return userEntity;
    }
}

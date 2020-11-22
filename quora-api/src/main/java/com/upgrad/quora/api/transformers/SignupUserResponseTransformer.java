package com.upgrad.quora.api.transformers;

import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.common.LoginStatus;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public  class SignupUserResponseTransformer {
    public SignupUserResponse transform(UserEntity userEntity){
        LoginStatus loginStatus = LoginStatus.valueOf(userEntity.getLoginStatus());
        SignupUserResponse signupUserResponse = new SignupUserResponse().id(userEntity.getUuid()).status(loginStatus.getMessage());
        return signupUserResponse;
    }
}

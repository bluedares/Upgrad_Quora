package com.upgrad.quora.api.transformers;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.service.common.LoginStatus;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class SigninResponseTransformer {
    public SigninResponse transform(UserEntity userEntity){
        LoginStatus loginStatus = LoginStatus.valueOf(userEntity.getLoginStatus());
        SigninResponse signinResponse = new SigninResponse().id(userEntity.getUuid()).message(loginStatus.getMessage());
        return signinResponse;
    }
}

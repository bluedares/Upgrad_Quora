package com.upgrad.quora.api.transformers;

import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.service.common.LoginStatus;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class SignoutResponseTransformer {
    public SignoutResponse transform(UserAuthEntity userAuthEntity){
        LoginStatus loginStatus = LoginStatus.valueOf(userAuthEntity.getUser().getLoginStatus());
        SignoutResponse signoutResponse = new SignoutResponse().id(userAuthEntity.getUuid()).message(loginStatus.getMessage());
        return signoutResponse;
    }
}

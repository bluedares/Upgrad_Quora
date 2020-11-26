package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.InputUserCredentials;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Base64;

@Service
public class TokenService {
    public String generateToken(String encryptedPassword, String userUuid, ZonedDateTime issuedDateTime, ZonedDateTime expiredDateTime){
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
        return jwtTokenProvider.generateToken(userUuid,issuedDateTime,expiredDateTime);
    }

    public InputUserCredentials getUserCredentialsFromBasicToken(String basicToken){
        String[] authorizationArr = basicToken.split("Basic ");
        byte[] decode = Base64.getDecoder().decode(authorizationArr[1]);
        String decodedString = new String(decode);
        String[] authArr = decodedString.split(":");
        InputUserCredentials inputUserCredentials = new InputUserCredentials();
        inputUserCredentials.setInputUserName(authArr[0]);
        inputUserCredentials.setInputPassword(authArr[1]);
        return inputUserCredentials;
    }

    public String getBearerToken(String accessToken){
        String bearerToken = null;
        try {
            String[] accessTokenArr = accessToken.split("Bearer ");
            bearerToken = accessTokenArr[1];
        }
        catch (ArrayIndexOutOfBoundsException exe){
            bearerToken = accessToken;
        }
        return bearerToken;
    }
}

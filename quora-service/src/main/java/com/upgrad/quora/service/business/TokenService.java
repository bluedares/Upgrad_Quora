package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.InputUserCredentials;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Base64;

/**
 * This is a service class that deals with generating basic Token for the login credentials and retrieving bearer token for validating the user before
 * allowing him to perform any operation.
 */
@Service
public class TokenService {
    /**
     *
     * @param encryptedPassword
     * @param userUuid
     * @param issuedDateTime
     * @param expiredDateTime
     * @return
     * This method generates access token for the given user name and password.
     * This token is valid for the given time duration between @param issuedDateTime and @param expiredDateTime
     */
    public String generateToken(String encryptedPassword, String userUuid, ZonedDateTime issuedDateTime, ZonedDateTime expiredDateTime){
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
        return jwtTokenProvider.generateToken(userUuid,issuedDateTime,expiredDateTime);
    }

    /**
     *
     * @param basicToken
     * @return
     * Given the encoded token (with user name and encrypted password), this method retrieves the same. This method is called during user Authentication.
     */
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

    /**
     *
     * @param accessToken
     * @return
     * This method fetches the bearer Access Token.
     */
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

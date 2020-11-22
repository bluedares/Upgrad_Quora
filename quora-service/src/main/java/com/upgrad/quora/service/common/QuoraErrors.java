package com.upgrad.quora.service.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum QuoraErrors {
    USER_NAME_ALREADY_EXISTS("USER_NAME_ALREADY_EXISTS","1001",HttpStatus.BAD_REQUEST,"User name already exists. Please choose another user name"),
    USER_EMAIL_ALREADY_EXISTS("USER_EMAIL_ALREADY_EXISTS","1002",HttpStatus.BAD_REQUEST,"Email already exists. Please register through alternate email id"),
    USER_NOT_FOUND("USER_NOT_FOUND","2001",HttpStatus.UNAUTHORIZED,"User has not registered."),
    INCORRECT_PASSWORD("INCORRECT_PASSWORD","2002",HttpStatus.UNAUTHORIZED,"Entered password is not correct. Please enter correct password"),
    INVALID_ACCESS_TOKEN("INVALID_ACCESS_TOKEN","2003",HttpStatus.UNAUTHORIZED,"Access Token is invalid. You are not authorized."),
    USER_NOT_SIGNED_IN("USER_NOT_SIGNED_IN","2004",HttpStatus.CONFLICT,"You are not logged in the system.");
    @Getter
    private String errorCode;
    @Getter
    private String internalErrorCode;
    @Getter
    private HttpStatus status;
    @Getter
    private String messsage;
}

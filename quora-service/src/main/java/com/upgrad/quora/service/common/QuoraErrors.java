package com.upgrad.quora.service.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum QuoraErrors {
    USER_NAME_ALREADY_EXISTS("USER_NAME_ALREADY_EXISTS","SUP-001",HttpStatus.BAD_REQUEST,"User name already exists. Please choose another user name"),
    USER_EMAIL_ALREADY_EXISTS("USER_EMAIL_ALREADY_EXISTS","SUP-002",HttpStatus.BAD_REQUEST,"Email already exists. Please register through alternate email id");
    @Getter
    private String errorCode;
    @Getter
    private String internalErrorCode;
    @Getter
    private HttpStatus status;
    @Getter
    private String messsage;
}

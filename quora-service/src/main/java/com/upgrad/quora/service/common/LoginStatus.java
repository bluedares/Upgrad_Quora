package com.upgrad.quora.service.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum LoginStatus {
    REGISTERED("USER SUCCESSFULLY REGISTERED"),
    LOGGED_IN("USER HAS LOGGED IN SUCCESSFULLY"),
    LOGGED_OUT("USER HAS LOGGED OUT SUCCESSFULLY");

    @Getter
    private String message;

}

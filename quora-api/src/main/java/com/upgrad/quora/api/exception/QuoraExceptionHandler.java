package com.upgrad.quora.api.exception;


import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class QuoraExceptionHandler {


    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> signupRestrictedException(SignUpRestrictedException exception, WebRequest webRequest){
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exception.getCode())
                .message(exception.getErrorMessage()),exception.getStatus());
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(AuthenticationFailedException exception,WebRequest webRequest){
        return new ResponseEntity<ErrorResponse>
                (new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()),exception.getStatus());
    }

    @ExceptionHandler(SignOutRestrictedException.class)
    public ResponseEntity<ErrorResponse> signoutRestrictedException(SignOutRestrictedException exception,WebRequest webRequest){
        return new ResponseEntity<ErrorResponse>
                (new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()),exception.getStatus());
    }
}

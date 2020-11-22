package com.upgrad.quora.api.exception;


import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class QuoraExceptionHandler {

    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> signupRestrictedException(SignUpRestrictedException signupRestrictedException, WebRequest webRequest){
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(signupRestrictedException.getCode())
                .message(signupRestrictedException.getErrorMessage()),signupRestrictedException.getStatus());
    }
}

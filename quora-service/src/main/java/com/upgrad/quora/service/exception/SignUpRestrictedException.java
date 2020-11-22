package com.upgrad.quora.service.exception;

import com.upgrad.quora.service.common.QuoraErrors;
import org.springframework.http.HttpStatus;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * SignUpRestrictedException is thrown when a user is restricted to register in the application due to repeated username or email.
 */
public class SignUpRestrictedException extends Exception {
    private final String code;
    private final String errorMessage;
    private final HttpStatus status;


    public SignUpRestrictedException(QuoraErrors error){
        this.code = error.getErrorCode();
        this.errorMessage = error.getMesssage();
        this.status = error.getStatus();
    }
    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }

    @Override
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
    }

    public String getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}


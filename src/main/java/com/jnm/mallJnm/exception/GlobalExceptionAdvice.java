package com.jnm.mallJnm.exception;

import com.jnm.mallJnm.controller.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;


//@RestControllerAdvice
public class GlobalExceptionAdvice {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> defaultException(Exception e) {
        return new ResponseEntity<>(Result.fail(-1, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void accessDeniedException(AccessDeniedException e) {
        throw e;
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<?> serverException(ServerException e) {
        return new ResponseEntity<>(Result.fail(e.getCode(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ValidatedException.class})
    public Result badRequestException(Exception e) {
        int code;
        String msg;
        if (e instanceof MethodArgumentNotValidException) {
            code = 201;
            msg = ((MethodArgumentNotValidException) e).getBindingResult().getFieldError().getDefaultMessage();
        } else {
            code = ((ValidatedException) e).getCode();
            msg = e.getMessage();
        }
        return Result.fail(code, msg);
    }
    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public Result sqlException(Exception e){
        return Result.fail(201, e.getMessage());
    }
}

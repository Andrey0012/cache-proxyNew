package com.example.cacheproxynew.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(BadRequestElementException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String badRequestHandleException (BadRequestElementException e) {
        return e.getMessage();
    }

    @ExceptionHandler (NotFoundElementException.class)
    @ResponseStatus (HttpStatus.NOT_FOUND)
    @ResponseBody
    public String notFoundHandleException (NotFoundElementException e) {
        return e.getMessage();
    }


    @ExceptionHandler (MethodNotAllowedElementException.class)
    @ResponseStatus (HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public String methodNotAllowedExceptionElementException (MethodNotAllowedElementException e) {
        return e.getMessage();
    }

}

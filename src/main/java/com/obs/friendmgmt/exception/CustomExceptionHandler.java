package com.obs.friendmgmt.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {BadRequestException.class})
    protected ResponseEntity<Object> handleBadRequest(RuntimeException ex, WebRequest request) {
        return buildResponse(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {RecordNotFoundException.class})
    protected ResponseEntity<Object> handleRecordNotFoundRequest(RuntimeException ex, WebRequest request) {
        return buildResponse(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {DuplicateRecordException.class})
    protected ResponseEntity<Object> handleDuplicateRecordRequest(RuntimeException ex, WebRequest request) {
        return buildResponse(ex, request, HttpStatus.CONFLICT);
    }

    private ResponseEntity<Object> buildResponse(Exception ex, WebRequest request, HttpStatus statusCode) {
        ErrorMessageDto body = new ErrorMessageDto().setMessage(ex.getMessage());
        return handleExceptionInternal(ex, body,
                new HttpHeaders(), statusCode, request);
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        final List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        ErrorMessageDto message = new ErrorMessageDto()
                .setMessage("Validation failed")
                .setDetail(fieldErrors.stream()
                        .map(fieldError -> new FieldErrorMessage(fieldError.getField(), fieldError.getDefaultMessage()))
                        .collect(Collectors.toList()));


        return new ResponseEntity(fieldErrors.isEmpty() ? ex : message, headers, status);
    }

}

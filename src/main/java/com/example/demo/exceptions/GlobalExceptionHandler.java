package com.example.demo.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j //автоматически создает логгер (из библиотеки SLF4J (Simple Logging Facade for Java)
@Order(Ordered.HIGHEST_PRECEDENCE) //гарантирует, что компонент будет выполнен или проинициализирован в первую очередь
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
                return super.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults()
                        .including(ErrorAttributeOptions.Include.MESSAGE)
                );
            }
        };
    }

    @ExceptionHandler(CustomExсeption.class)
    public void handleCustomException (HttpServletResponse response, CustomExсeption ex) throws IOException {
        response.sendError(ex.getStatus().value(), ex.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorMessage> handleMissingServletRequestParameterException (MissingServletRequestParameterException ex) throws IOException {
        String parameterName = ex.getParameterName();

        log.error("{} parameter is missing", parameterName);

        return ResponseEntity.status(400)
                .body(new ErrorMessage(String.format("parameter is missing: %s", parameterName)));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentTypeMismatchException (MethodArgumentTypeMismatchException ex) throws IOException {
        String parameterName = ex.getParameter().getParameterName();

        log.error("wrong data for parameter {}", parameterName);

        return ResponseEntity.status(400)
                .body(new ErrorMessage(String.format("wrong data for parameter: %s", parameterName)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException (MethodArgumentNotValidException ex) throws IOException {

        FieldError fieldError = ex.getFieldError();

        String messege = fieldError != null ? fieldError.getDefaultMessage() : ex.getMessage();


        return ResponseEntity.status(400)
                .body(new ErrorMessage(messege));
    }


}

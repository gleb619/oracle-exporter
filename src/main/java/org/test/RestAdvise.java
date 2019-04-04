package org.test;

import com.google.common.collect.ImmutableMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class RestAdvise {

    @ExceptionHandler(Exception.class)
    public Map handleNotFoundException(Exception ex) {
        return ImmutableMap.of(
                "message", ex.getMessage()
        );
    }

}

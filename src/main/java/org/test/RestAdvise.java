package org.test;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class RestAdvise {

    @ExceptionHandler(Exception.class)
    public Map handleNotFoundException(Exception ex) {
        log.error("ERROR: ", ex);
        return ImmutableMap.of(
                "message", ex.getMessage()
        );
    }

}

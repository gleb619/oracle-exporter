package org.test.controller;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@ResponseBody
@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Map handleException(HttpServletRequest request, Exception e) {
        log.error("ERROR: ", e);
        return ImmutableMap.of(
                "datetime", LocalDateTime.now(),
                "message", MoreObjects.firstNonNull(e.getMessage(), e.getClass().getSimpleName()),
                "url", request.getRequestURL()
        );
    }

}

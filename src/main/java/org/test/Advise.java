package org.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ResponseBody
@ControllerAdvice
public class Advise {

    @ExceptionHandler(value = Exception.class)
    public Map defaultErrorHandler(HttpServletRequest request, Exception e) {
        log.error("ERROR: ", e);
        Map output = new HashMap();

        output.put("datetime", new Date());
        output.put("message", e.getMessage());
        output.put("url", request.getRequestURL());

        return output;
    }

}

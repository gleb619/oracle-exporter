package org.test.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.test.config.AppProperties;
import org.test.service.InsertsHelper;
import org.test.service.JythonService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.ACCEPT_RANGES;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.test.config.Constants.APPLICATION_X_SQL_CHARSET_UTF_8;
import static org.test.config.Constants.BYTES;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Controller {

    private final JythonService jythonService;
    private final InsertsHelper insertsHelper;

    @GetMapping("/dump/download")
    public void dump(HttpServletResponse response) throws IOException {
        jythonService.exportStandard(response.getOutputStream(), JythonService.SCHEMA_ORA_PY);
        insertsHelper.generateStandard(response.getOutputStream());
    }

    @GetMapping("/dump/download/{filename}")
    public void download(HttpServletResponse response, @PathVariable String filename) throws IOException {
        response.addHeader(ACCEPT_RANGES, BYTES);
        response.addHeader(CONTENT_TYPE, APPLICATION_X_SQL_CHARSET_UTF_8);
        jythonService.exportStandard(response.getOutputStream(), JythonService.SCHEMA_ORA_PY);
        insertsHelper.generateStandard(response.getOutputStream());
    }

    @PostMapping("/dump/download/{filename}")
    public void downloadCustom(HttpServletResponse response, @PathVariable String filename, @RequestBody AppProperties data) throws IOException {
        response.addHeader(ACCEPT_RANGES, BYTES);
        response.addHeader(CONTENT_TYPE, APPLICATION_X_SQL_CHARSET_UTF_8);
        jythonService.exportCustom(response.getOutputStream(), data, JythonService.SCHEMA_ORA_PY);
        insertsHelper.generateCustom(response.getOutputStream(), data);
    }

}

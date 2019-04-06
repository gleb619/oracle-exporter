package org.test.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;
import org.test.config.AppProperties;

import javax.servlet.ServletOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JythonService {

    public static final String SCHEMA_ORA_PY = "schema_ora.py";

    private final ObjectFactory<PythonInterpreter> pythonInterpreter;
    private final AppProperties appProperties;

    public void exportCustom(ServletOutputStream outputStream, AppProperties data, String script) {
        AppProperties properties = data.merge(appProperties);
        export(outputStream, properties, script);
    }

    public void exportStandard(OutputStream outputStream, String script) {
        export(outputStream, appProperties, script);
    }

    private void export(OutputStream outputStream, AppProperties properties, String script) {
        try {
            InputStream initInputStream = this.getClass().getClassLoader().getResourceAsStream(script);
            PythonInterpreter interpreter = pythonInterpreter.getObject();
            interpreter.exec(prepareArgument(properties));
            interpreter.setOut(outputStream);
            interpreter.execfile(initInputStream);
            log.info("ddl was generated for {}, {}", properties.url, properties.username);
        } catch (Exception e) {
            log.error("ERROR: ", e);
            e.printStackTrace(new PrintWriter(outputStream));
        }
    }

    private String prepareArgument(AppProperties appProperties) {
        log.info("Prepare to generate ddl for {}, {}", appProperties.url, appProperties.username);
        return String.format("import sys\nsys.argv = ['schema_ora.py', '%s', '%s', '%s', '--show_sql']",
                appProperties.url,
                appProperties.username,
                appProperties.password);
    }

}
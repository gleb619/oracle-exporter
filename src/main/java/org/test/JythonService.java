package org.test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import lombok.RequiredArgsConstructor;
import org.python.util.PythonInterpreter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;
import org.test.config.AppProperties;

@Component
@RequiredArgsConstructor
public class JythonService {
    
    private final ObjectFactory<PythonInterpreter> pythonInterpreter;
    private final AppProperties appProperties;

    public String export() {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        export();
        return outStream.toString();
    }

    public void export(OutputStream outputStream) {
        try {
            InputStream initInputStream = this.getClass().getClassLoader().getResourceAsStream("schema_ora.py");
            PythonInterpreter interpreter = pythonInterpreter.getObject();
//        interpreter.exec("import sys\nsys.argv = ['schema_ora.py', 'jdbc:oracle:thin:@192.168.233.131:1521:XE', 'DMS', 'DMS', '--show_sql']");
            interpreter.exec(prepareArgument());
            interpreter.setOut(outputStream);
            interpreter.execfile(initInputStream);
        } catch (BeansException e) {
            e.printStackTrace(new PrintWriter(outputStream));
        }
    }

    private String prepareArgument() {
        return String.format("import sys\nsys.argv = ['schema_ora.py', '%s', '%s', '%s', '--show_sql']",
                appProperties.url,
                appProperties.username,
                appProperties.password);
    }

}
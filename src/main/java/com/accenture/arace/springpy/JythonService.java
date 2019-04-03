package com.accenture.arace.springpy;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.python.core.*;
import org.python.util.PythonInterpreter;
import org.springframework.stereotype.Component;

@Component
public class JythonService {
    
    PythonInterpreter pythonInterpreter;

    public JythonService() {
        pythonInterpreter = new PythonInterpreter();
    }

    public void execScriptAsInputStream (InputStream inputStream) {
        pythonInterpreter.execfile(inputStream);
    }

    public String execMethodInPyClass(String fileName, String className, String methodName) {
        pythonInterpreter.exec("from main import Main");
        PyClass mainClass = (PyClass) pythonInterpreter.get("Main");
        PyObject main = mainClass.__call__();
        PyObject pyObject = main.invoke("launchAndDebug", new PyString("svn://192.168.37.135/test"));
        return pyObject.toString();
    }

    public String execMethodInPyClass2() {
        InputStream initInputStream = this.getClass().getClassLoader().getResourceAsStream("schema_ora.py");
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec("import sys\nsys.argv = ['schema_ora.py', 'jdbc:oracle:thin:@192.168.233.131:1521:XE', 'DMS', 'DMS', '--show_sql']");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        interpreter.setOut(outStream);
        interpreter.execfile(initInputStream);
        return outStream.toString();
    }

}
package com.accenture.arace.springpy;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.python.google.common.collect.ImmutableBiMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RestApiController {

    @Autowired
    JythonService jythonService;

    @GetMapping("/svn")
    public Map goSvnGo() {
        HashMap map = new HashMap();
        map.put("data", jythonService.execMethodInPyClass2());
        return map;
    }

}

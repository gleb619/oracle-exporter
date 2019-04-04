package org.test;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.OutputStream;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Controller {

    private final JythonService jythonService;
    private final GenerateInsertStatementsHelper generateInsertStatementsHelper;

    @GetMapping("/dump/download")
    public void dump(OutputStream outputStream) {
        jythonService.export(outputStream);
        generateInsertStatementsHelper.generate(outputStream);
    }

    @GetMapping("/dump/save")
    public Map save() {
        return ImmutableMap.of("data", jythonService.export());
    }

}

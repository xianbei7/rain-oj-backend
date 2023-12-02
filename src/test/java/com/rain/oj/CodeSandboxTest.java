package com.rain.oj;

import com.rain.oj.judge.codesandbox.CodeSandbox;
import com.rain.oj.judge.codesandbox.impl.ExampleCodeSandbox;
import com.rain.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.rain.oj.judge.codesandbox.model.ExecuteCodeResponse;
import com.rain.oj.model.enums.ProgrammingLanguageEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class CodeSandboxTest {
    @Autowired
    private Map<String, CodeSandbox> codeSandboxMap;
    @Test
    void executeCode() {
        CodeSandbox codeSandbox = new ExampleCodeSandbox();
        String code = "int main(){}";
        String language = ProgrammingLanguageEnum.JAVA.getText();
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        Assertions.assertNotNull(executeCodeResponse);
    }
}

package com.rain.oj.judge.codesandbox.impl;

import com.rain.oj.judge.codesandbox.CodeSandbox;
import com.rain.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.rain.oj.judge.codesandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Service;

/**
 * 第三方代码沙箱（调用晚上现成的接口）
 */
@Service
public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}

package com.rain.oj.judge.codesandbox;

import com.rain.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.rain.oj.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * 定义接口，提高通用性
 */
public interface CodeSandbox {
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeResquest);
}

package com.rain.oj.judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.rain.oj.common.ErrorCode;
import com.rain.oj.exception.BusinessException;
import com.rain.oj.judge.codesandbox.CodeSandbox;
import com.rain.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.rain.oj.judge.codesandbox.model.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 远程代码沙箱
 */
@Service
public class RemoteCodeSandbox implements CodeSandbox {

    @Value("${code-sandbox.auth.header}")
    private String authRequestHeader;

    @Value("${code-sandbox.auth.secret}")
    private String authRequestSecret;

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String url = "http://localhost:8900/code/sandbox/execute";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String responseStr = HttpUtil.createPost(url)
                .header(authRequestHeader, authRequestSecret)
                .body(json)
                .execute()
                .body();
        if (StringUtils.isBlank(responseStr)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR);
        }
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }
}

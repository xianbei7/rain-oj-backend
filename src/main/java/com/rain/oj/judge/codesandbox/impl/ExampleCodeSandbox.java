package com.rain.oj.judge.codesandbox.impl;

import com.rain.oj.judge.codesandbox.CodeSandbox;
import com.rain.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.rain.oj.judge.codesandbox.model.ExecuteCodeResponse;
import com.rain.oj.judge.codesandbox.model.JudgeInfo;
import com.rain.oj.judge.codesandbox.model.JudgeResult;
import com.rain.oj.model.enums.JudgeInfoMessageEnum;
import com.rain.oj.model.enums.QuestionSubmitStatusEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 实例代码沙箱
 */
@Service
public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();

        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.ACCEPTED.getValue());
        List<JudgeInfo> judgeInfoList = new ArrayList<>();
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setTime(100L);
        judgeInfo.setMemory(100L);
        judgeInfoList.add(judgeInfo);
        executeCodeResponse.setJudgeInfoList(judgeInfoList);
        return executeCodeResponse;
    }
}

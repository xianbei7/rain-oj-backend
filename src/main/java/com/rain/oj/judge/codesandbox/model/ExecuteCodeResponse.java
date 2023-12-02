package com.rain.oj.judge.codesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 执行代码响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeResponse {

    /**
     * 执行状态
     */
    private Integer status;

    /**
     * 错误类型
     */
    private String errorType;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 判题信息
     */
    private List<JudgeInfo> judgeInfoList;

    /**
     * 输出用例
     */
    private List<String> outputList;
}

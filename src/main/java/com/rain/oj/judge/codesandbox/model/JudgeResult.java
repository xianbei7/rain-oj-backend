package com.rain.oj.judge.codesandbox.model;

import lombok.Data;

/**
 * 判题信息
 */
@Data
public class JudgeResult {

    /**
     * 程序执行信息
     */
    private String message;

    /**
     * 通过率
     */
    private Double correctRate;
}

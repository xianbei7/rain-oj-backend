package com.rain.oj.model.dto.question;

import lombok.Data;

/**
 * 题目配置
 */
@Data
public class JudgeConfig {
    /**
     * 时间限制（ms）
     */
    private int timeLimit;
    /**
     * 内存限制（kb）
     */
    private int memoryLimit;

    /**
     * 堆栈限制（kb）
     */
    private int stackLimit;
}

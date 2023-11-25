package com.rain.oj.model.dto.questionsubmit;

import lombok.Data;

/**
 * 判题信息
 */
@Data
public class JudgeInfo {

    /**
     * 程序执行信息
     */
    private String message;
    /**
     * 消耗时间
     */
    private Integer time;
    /**
     * 消耗内存
     */
    private Integer memory;
}

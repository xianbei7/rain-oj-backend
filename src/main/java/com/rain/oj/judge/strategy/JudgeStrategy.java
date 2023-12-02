package com.rain.oj.judge.strategy;

import com.rain.oj.judge.codesandbox.model.JudgeResult;

public interface JudgeStrategy {
    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    JudgeResult doJudge(JudgeContext judgeContext);
}

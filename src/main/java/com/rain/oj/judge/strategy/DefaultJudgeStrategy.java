package com.rain.oj.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.rain.oj.judge.codesandbox.model.JudgeResult;
import com.rain.oj.model.dto.question.JudgeCase;
import com.rain.oj.model.dto.question.JudgeConfig;
import com.rain.oj.judge.codesandbox.model.JudgeInfo;
import com.rain.oj.model.entity.Question;
import com.rain.oj.model.enums.JudgeInfoMessageEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认判题策略
 */
public class DefaultJudgeStrategy implements JudgeStrategy {
    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    @Override
    public JudgeResult doJudge(JudgeContext judgeContext) {
        JudgeResult judgeResult = new JudgeResult();
        List<JudgeInfo> judgeInfoList = judgeContext.getJudgeInfo();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Integer needMemoryLimit = judgeConfig.getMemoryLimit();
        Integer needTimeLimit = judgeConfig.getTimeLimit();

        boolean setMessage = false;
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        int totalCaseCount = judgeCaseList.size();
        int correctCaseCount = 0;
        // 依次判断每一项输出和预期输出是否相等
        for (int i = 0; i < totalCaseCount; i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            if (!judgeCase.getOutput().equals(outputList.get(i))) {
                if (!setMessage) {
                    judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                    setMessage = true;
                }
            } else {
                JudgeInfo judgeInfo = judgeInfoList.get(i);
                Long memory = judgeInfo.getMemory();
                Long time = judgeInfo.getTime();
                if (memory > needMemoryLimit) {
                    if (!setMessage) {
                        judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
                        setMessage = true;
                    }
                } else if (time > needTimeLimit) {
                    if (!setMessage) {
                        judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
                        setMessage = true;
                    }
                } else {
                    correctCaseCount++;
                }
            }
        }
        judgeResult.setCorrectRate((double) correctCaseCount / totalCaseCount);
        judgeResult.setMessage(judgeInfoMessageEnum.getValue());
        return judgeResult;
    }
}

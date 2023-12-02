package com.rain.oj.judge;

import com.rain.oj.judge.codesandbox.model.JudgeResult;
import com.rain.oj.judge.strategy.DefaultJudgeStrategy;
import com.rain.oj.judge.strategy.JavaLanguageJudgeStrategy;
import com.rain.oj.judge.strategy.JudgeContext;
import com.rain.oj.judge.strategy.JudgeStrategy;
import com.rain.oj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeResult doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}


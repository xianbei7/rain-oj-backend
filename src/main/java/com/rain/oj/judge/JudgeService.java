package com.rain.oj.judge;

import com.rain.oj.model.entity.QuestionSubmit;

public interface JudgeService {
    QuestionSubmit doJudge(long questionSubmitId);
}

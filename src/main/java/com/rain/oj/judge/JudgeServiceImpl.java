package com.rain.oj.judge;

import cn.hutool.json.JSONUtil;
import com.rain.oj.common.ErrorCode;
import com.rain.oj.exception.BusinessException;
import com.rain.oj.judge.codesandbox.CodeSandbox;
import com.rain.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.rain.oj.judge.codesandbox.model.ExecuteCodeResponse;
import com.rain.oj.judge.codesandbox.model.JudgeResult;
import com.rain.oj.judge.strategy.JudgeContext;
import com.rain.oj.model.dto.question.JudgeCase;
import com.rain.oj.model.entity.Question;
import com.rain.oj.model.entity.QuestionSubmit;
import com.rain.oj.model.enums.CodeSandboxEnum;
import com.rain.oj.model.enums.QuestionSubmitStatusEnum;
import com.rain.oj.service.QuestionService;
import com.rain.oj.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private JudgeManager judgeManager;

    @Autowired
    private Map<String, CodeSandbox> codeSandboxMap;

    private CodeSandbox getSandbox(CodeSandboxEnum codeSandboxEnum) {
        return codeSandboxMap.get(codeSandboxEnum.getValue() + "CodeSandbox");
    }

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {

        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题");
        }
        // todo 可扩展：分布式锁
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目状态更新失败");
        }
        CodeSandbox codeSandbox = getSandbox(CodeSandboxEnum.EXAMPLE);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        // 调用代码沙箱
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);

        // 根据沙箱的执行结果。设置题目的判题状态
        List<String> outputList = executeCodeResponse.getOutputList();
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfoList());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
        JudgeResult judgeResult = judgeManager.doJudge(judgeContext);
        // 修改数据库中的判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.ACCEPTED.getValue());
        questionSubmitUpdate.setJudgeResult(JSONUtil.toJsonStr(judgeResult));
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(executeCodeResponse.getJudgeInfoList()));
        update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        return questionSubmitService.getById(questionId);
    }
}

package com.rain.oj.model.vo;

import cn.hutool.json.JSONUtil;
import com.rain.oj.judge.codesandbox.model.JudgeResult;
import com.rain.oj.model.entity.QuestionSubmit;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * 题目提交封装类
 */
@Data
public class QuestionSubmitVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 提交用户 id
     */
    private Long userId;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 判题信息（json 数组）
     */
    private JudgeResult judgeResult;

    /**
     * 判题状态（0 - 待判题、1 - 判题中、2  成功、3 - 失败）
     */
    private Integer status;

    /**
     * 提交用户信息
     */
    private UserVO userVO;

    /**
     * 提交题目信息
     */
    private QuestionVO questionVO;

    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     *
     * @param questionSubmitVO
     * @return
     */
    public static QuestionSubmit voToObj(QuestionSubmitVO questionSubmitVO) {
        if (questionSubmitVO == null) {
            return null;
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        BeanUtils.copyProperties(questionSubmitVO, questionSubmit);
        JudgeResult judgeResult = questionSubmitVO.getJudgeResult();
        if (judgeResult != null) {
            questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeResult));
        }
        return questionSubmit;
    }

    /**
     * 对象转包装类
     *
     * @param questionSubmit
     * @return
     */
    public static QuestionSubmitVO objToVo(QuestionSubmit questionSubmit) {
        if (questionSubmit == null) {
            return null;
        }
        QuestionSubmitVO questionSubmitVO = new QuestionSubmitVO();
        BeanUtils.copyProperties(questionSubmit, questionSubmitVO);
        String judgeResult = questionSubmit.getJudgeResult();
        questionSubmitVO.setJudgeResult(JSONUtil.toBean(judgeResult, JudgeResult.class));
        return questionSubmitVO;
    }

}
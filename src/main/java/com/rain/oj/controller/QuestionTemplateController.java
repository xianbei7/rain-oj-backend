package com.rain.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rain.oj.common.BaseResponse;
import com.rain.oj.common.ErrorCode;
import com.rain.oj.common.ResultUtils;
import com.rain.oj.exception.BusinessException;
import com.rain.oj.exception.ThrowUtils;
import com.rain.oj.model.bo.QuestionFavourBO;
import com.rain.oj.model.dto.question.QuestionQueryRequest;
import com.rain.oj.model.dto.questionfavour.QuestionFavourQueryRequest;
import com.rain.oj.model.entity.User;
import com.rain.oj.model.vo.QuestionFavourVO;
import com.rain.oj.model.vo.QuestionVO;
import com.rain.oj.service.QuestionFavourService;
import com.rain.oj.service.QuestionService;
import com.rain.oj.service.QuestionTemplateService;
import com.rain.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目收藏接口
 */
@RestController
@RequestMapping("/question/template")
@Slf4j
public class QuestionTemplateController {

    @Resource
    private QuestionTemplateService questionTemplateService;
    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    /**
     * 根据questionId和language获取代码模板
     *
     * @param questionId
     * @param language
     * @param request
     * @return codeTemplate 代码模板
     */
    @GetMapping("/")
    public BaseResponse<String> getQuestionTemplate(Long questionId, String language, HttpServletRequest request) {
        if (questionId == null || questionId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isBlank(language)) {
            return ResultUtils.success("");
        }
        // 登录才能操作
        userService.getLoginUser(request);
        return ResultUtils.success(questionTemplateService.getCodeTemplate(questionId, language));
    }
}

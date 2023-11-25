package com.rain.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rain.oj.common.BaseResponse;
import com.rain.oj.common.ErrorCode;
import com.rain.oj.common.ResultUtils;
import com.rain.oj.exception.BusinessException;
import com.rain.oj.exception.ThrowUtils;
import com.rain.oj.model.dto.question.QuestionQueryRequest;
import com.rain.oj.model.dto.questionfavour.QuestionFavourQueryRequest;
import com.rain.oj.model.entity.Question;
import com.rain.oj.model.entity.User;
import com.rain.oj.model.vo.QuestionVO;
import com.rain.oj.service.QuestionFavourService;
import com.rain.oj.service.QuestionService;
import com.rain.oj.service.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 题目收藏接口
 */
@RestController
@RequestMapping("/question/favour")
@Slf4j
public class QuestionFavourController {

    @Resource
    private QuestionFavourService questionFavourService;

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    /**
     * 收藏 / 取消收藏
     *
     * @param questionId
     * @param request
     * @return resultNum 收藏变化数
     */
    @GetMapping("/")
    public BaseResponse<Integer> doQuestionFavour(Long questionId,HttpServletRequest request) {
        if (questionId == null || questionId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能操作
        final User loginUser = userService.getLoginUser(request);
        int result = questionFavourService.doQuestionFavour(questionId, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 获取我收藏的题目列表
     *
     * @param questionQueryRequest
     * @param request
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<QuestionVO>> listMyFavourQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
            HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionFavourService.listFavourQuestionByPage(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest), loginUser.getId());
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 获取用户收藏的题目列表
     *
     * @param questionFavourQueryRequest
     * @param request
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionVO>> listFavourQuestionByPage(@RequestBody QuestionFavourQueryRequest questionFavourQueryRequest,
            HttpServletRequest request) {
        if (questionFavourQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = questionFavourQueryRequest.getCurrent();
        long size = questionFavourQueryRequest.getPageSize();
        Long userId = questionFavourQueryRequest.getUserId();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20 || userId == null, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionFavourService.listFavourQuestionByPage(new Page<>(current, size),
                questionService.getQueryWrapper(questionFavourQueryRequest.getQuestionQueryRequest()), userId);
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }
}

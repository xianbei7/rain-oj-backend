package com.rain.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.rain.oj.common.ErrorCode;
import com.rain.oj.constant.CommonConstant;
import com.rain.oj.exception.BusinessException;
import com.rain.oj.exception.ThrowUtils;
import com.rain.oj.mapper.QuestionFavourMapper;
import com.rain.oj.mapper.QuestionMapper;
import com.rain.oj.mapper.QuestionThumbMapper;
import com.rain.oj.model.dto.question.QuestionQueryRequest;
import com.rain.oj.model.entity.Question;
import com.rain.oj.model.entity.QuestionFavour;
import com.rain.oj.model.entity.QuestionThumb;
import com.rain.oj.model.entity.User;
import com.rain.oj.model.vo.QuestionVO;
import com.rain.oj.model.vo.UserVO;
import com.rain.oj.service.QuestionService;
import com.rain.oj.service.UserService;
import com.rain.oj.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;


/**
 *
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {
    private final static Gson GSON = new Gson();

    @Resource
    private UserService userService;
    @Resource
    private QuestionThumbMapper questionThumbMapper;
    @Resource
    private QuestionFavourMapper questionFavourMapper;

    /**
     * 校验题目是否合法
     *
     * @param question
     * @param add
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }
    }

    /**
     * 获取查询包装类（）
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public LambdaQueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUserId();
        String sortOrder = questionQueryRequest.getSortOrder();

        queryWrapper.like(StringUtils.isNotBlank(title), Question::getTitle, title);
        queryWrapper.like(StringUtils.isNotBlank(content), Question::getContent, content);
        queryWrapper.like(StringUtils.isNotBlank(answer), Question::getAnswer, answer);
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like(Question::getTags, "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), Question::getId, id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), Question::getUserId, userId);
        queryWrapper.eq(Question::getIsDelete, false);
        queryWrapper.orderBy(SqlUtils.validSortField(questionQueryRequest.getSortField()), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), question -> questionQueryRequest.getSortField());
        return queryWrapper;
    }

    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        QuestionVO questionVO = QuestionVO.objToVo(question);
        long questionId = question.getId();
        // 1. 关联查询用户信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionVO.setUserVO(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            // 获取点赞
            LambdaQueryWrapper<QuestionThumb> questionThumbQueryWrapper = new LambdaQueryWrapper<>();
            questionThumbQueryWrapper.in(QuestionThumb::getQuestionId, questionId);
            questionThumbQueryWrapper.eq(QuestionThumb::getUserId, loginUser.getId());
            QuestionThumb questionThumb = questionThumbMapper.selectOne(questionThumbQueryWrapper);
            questionVO.setHasThumb(questionThumb != null);
            // 获取收藏
            LambdaQueryWrapper<QuestionFavour> questionFavourQueryWrapper = new LambdaQueryWrapper<>();
            questionFavourQueryWrapper.in(QuestionFavour::getQuestionId, questionId);
            questionFavourQueryWrapper.eq(QuestionFavour::getUserId, loginUser.getId());
            QuestionFavour questionFavour = questionFavourMapper.selectOne(questionFavourQueryWrapper);
            questionVO.setHasFavour(questionFavour != null);
        }
        return questionVO;
    }

    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollectionUtils.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
        Map<Long, Boolean> questionIdHasThumbMap = new HashMap<>();
        Map<Long, Boolean> questionIdHasFavourMap = new HashMap<>();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            Set<Long> questionIdSet = questionList.stream().map(Question::getId).collect(Collectors.toSet());
            loginUser = userService.getLoginUser(request);
            // 获取点赞
            LambdaQueryWrapper<QuestionThumb> questionThumbQueryWrapper = new LambdaQueryWrapper<>();
            questionThumbQueryWrapper.in(QuestionThumb::getQuestionId, questionIdSet);
            questionThumbQueryWrapper.eq(QuestionThumb::getUserId, loginUser.getId());
            List<QuestionThumb> questionQuestionThumbList = questionThumbMapper.selectList(questionThumbQueryWrapper);
            questionQuestionThumbList.forEach(questionQuestionThumb -> questionIdHasThumbMap.put(questionQuestionThumb.getQuestionId(), true));
            // 获取收藏
            LambdaQueryWrapper<QuestionFavour> questionFavourQueryWrapper = new LambdaQueryWrapper<>();
            questionFavourQueryWrapper.in(QuestionFavour::getQuestionId, questionIdSet);
            questionFavourQueryWrapper.eq(QuestionFavour::getUserId, loginUser.getId());
            List<QuestionFavour> questionFavourList = questionFavourMapper.selectList(questionFavourQueryWrapper);
            questionFavourList.forEach(questionFavour -> questionIdHasFavourMap.put(questionFavour.getQuestionId(), true));
        }
        // 填充信息
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            QuestionVO questionVO = QuestionVO.objToVo(question);
            Long userId = question.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUserVO(userService.getUserVO(user));
            questionVO.setHasThumb(questionIdHasThumbMap.getOrDefault(question.getId(), false));
            questionVO.setHasFavour(questionIdHasFavourMap.getOrDefault(question.getId(), false));
            return questionVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

}
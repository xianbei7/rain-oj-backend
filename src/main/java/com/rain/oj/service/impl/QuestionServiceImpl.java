package com.rain.oj.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
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
import com.rain.oj.model.dto.question.JudgeCase;
import com.rain.oj.model.dto.question.JudgeConfig;
import com.rain.oj.model.dto.question.QuestionQueryRequest;
import com.rain.oj.model.dto.question.QuestionSaveRequest;
import com.rain.oj.model.entity.Question;
import com.rain.oj.model.entity.QuestionFavour;
import com.rain.oj.model.entity.QuestionThumb;
import com.rain.oj.model.entity.User;
import com.rain.oj.model.enums.QuestionDifficultyEnum;
import com.rain.oj.model.enums.QuestionTypeEnum;
import com.rain.oj.model.vo.DoQuestionVO;
import com.rain.oj.model.vo.QuestionVO;
import com.rain.oj.service.QuestionService;
import com.rain.oj.service.UserService;
import com.rain.oj.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 题目服务实现
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {
    private final static Gson GSON = new Gson();

    @Resource
    private UserService userService;
    @Resource
    private QuestionMapper questionMapper;
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
        Integer number = question.getNumber();
        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(number == null && StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
        if (number != null && number > 10000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编号太大");
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && answer.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && judgeCase.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > 8192) {
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
        String searchText = questionQueryRequest.getSearchText();
        Integer number = questionQueryRequest.getNumber();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String difficulty = questionQueryRequest.getDifficulty();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUserId();
        String sortOrder = questionQueryRequest.getSortOrder();
        String sortField = questionQueryRequest.getSortField();
        if (StringUtils.isNotBlank(searchText) && number == null && title == null && content == null && answer == null) {
            queryWrapper.like(Question::getNumber, searchText)
                    .or()
                    .like(Question::getTitle, searchText)
                    .or()
                    .like(Question::getContent, searchText)
                    .or()
                    .like(Question::getAnswer, searchText);
        } else {
            queryWrapper.like(number != null, Question::getNumber, number);
            queryWrapper.like(StringUtils.isNotBlank(title), Question::getTitle, title);
            queryWrapper.like(StringUtils.isNotBlank(content), Question::getContent, content);
            queryWrapper.like(StringUtils.isNotBlank(answer), Question::getAnswer, answer);
        }
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like(Question::getTags, "\"" + tag + "\"");
            }
        }
        if (StringUtils.isNotBlank(difficulty)) {
            QuestionDifficultyEnum difficultyEnum = QuestionDifficultyEnum.getEnumByText(difficulty);
            queryWrapper.eq(difficultyEnum != null, Question::getDifficulty, difficultyEnum.getValue());
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), Question::getUserId, userId);
        queryWrapper.eq(Question::getIsDelete, false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), getSortFieldColumn(sortField));
        return queryWrapper;
    }

    private SFunction<Question, ?> getSortFieldColumn(String sortField) {
        Map<String, SFunction<Question, ?>> map = new HashMap<>();
        {
            map.put("number", Question::getNumber);
            map.put("title", Question::getTitle);
            map.put("difficulty", Question::getDifficulty);
            map.put("submitNum", Question::getSubmitNum);
            map.put("acceptedNum", Question::getAcceptedNum);
            map.put("thumbNum", Question::getThumbNum);
            map.put("createTime", Question::getCreateTime);
            map.put("updateTime", Question::getUpdateTime);
        }
        return map.get(sortField);
    }

    @Override
    public DoQuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        DoQuestionVO doQuestionVO = DoQuestionVO.objToVo(question);
        long questionId = question.getId();
        // 2. 已登录，获取用户点赞、收藏状态
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            // 获取点赞
            LambdaQueryWrapper<QuestionThumb> questionThumbQueryWrapper = new LambdaQueryWrapper<>();
            questionThumbQueryWrapper.in(QuestionThumb::getQuestionId, questionId);
            questionThumbQueryWrapper.eq(QuestionThumb::getUserId, loginUser.getId());
            QuestionThumb questionThumb = questionThumbMapper.selectOne(questionThumbQueryWrapper);
            doQuestionVO.setHasThumb(questionThumb != null);
            // 获取收藏
            LambdaQueryWrapper<QuestionFavour> questionFavourQueryWrapper = new LambdaQueryWrapper<>();
            questionFavourQueryWrapper.in(QuestionFavour::getQuestionId, questionId);
            questionFavourQueryWrapper.eq(QuestionFavour::getUserId, loginUser.getId());
            QuestionFavour questionFavour = questionFavourMapper.selectOne(questionFavourQueryWrapper);
            doQuestionVO.setHasFavour(questionFavour != null);
        }
        return doQuestionVO;
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

    @Override
    public Boolean saveQuestion(QuestionSaveRequest questionSaveRequest, User loginUser, boolean add) {
        Integer number = questionMapper.getByNumber(questionSaveRequest.getNumber());
        if (add) {
            ThrowUtils.throwIf(number != null, ErrorCode.PARAMS_ERROR, "编号已存在");
        } else {
            // 修改，要求如果编号相同可以，不同且重复，报错
            long id = questionSaveRequest.getId();
            // 判断是否存在
            Question oldQuestion = getById(id);
            ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
            // 判断是否本人或管理员操作
            Long userId = oldQuestion.getUserId();
            if (!userId.equals(loginUser.getId()) && userService.isAdmin(loginUser)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有修改权限！");
            }
            // 如果编号不同且新编号重复，报错
            ThrowUtils.throwIf(oldQuestion.getNumber().equals(questionSaveRequest.getNumber()) && number != null, ErrorCode.NOT_FOUND_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionSaveRequest, question);
        String difficulty = questionSaveRequest.getDifficulty();
        QuestionDifficultyEnum difficultyEnum = QuestionDifficultyEnum.getEnumByText(difficulty);
        if (difficultyEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目难度设置错误");
        }
        question.setDifficulty(difficultyEnum.getValue());
        String type = questionSaveRequest.getType();
        QuestionTypeEnum questionTypeEnum = QuestionTypeEnum.getEnumByType(type);
        if (questionTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目类型设置错误");
        }
        question.setType(questionTypeEnum.getValue());
        List<String> tags = questionSaveRequest.getTags();
        JudgeConfig judgeConfig = questionSaveRequest.getJudgeConfig();
        List<JudgeCase> judgeCase = questionSaveRequest.getJudgeCase();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置不能为空");
        }
        if (CollectionUtil.isNotEmpty(judgeCase)) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例不能为空");
        }
        validQuestion(question, add);

        if (add) {
            question.setUserId(loginUser.getId());
            return save(question);
        } else {
            return updateById(question);
        }
    }

}
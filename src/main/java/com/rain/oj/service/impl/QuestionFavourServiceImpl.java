package com.rain.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rain.oj.common.ErrorCode;
import com.rain.oj.exception.BusinessException;
import com.rain.oj.mapper.QuestionFavourMapper;
import com.rain.oj.model.bo.QuestionFavourBO;
import com.rain.oj.model.entity.Question;
import com.rain.oj.model.entity.QuestionFavour;
import com.rain.oj.model.entity.User;
import com.rain.oj.model.vo.QuestionFavourVO;
import com.rain.oj.service.QuestionFavourService;
import com.rain.oj.service.QuestionService;
import com.rain.oj.service.UserService;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 题目收藏服务实现
 */
@Service
public class QuestionFavourServiceImpl extends ServiceImpl<QuestionFavourMapper, QuestionFavour>
        implements QuestionFavourService {

    @Resource
    private QuestionService questionService;
    @Resource
    private UserService userService;

    @Resource
    private QuestionFavourMapper questionFavourMapper;

    /**
     * 题目收藏
     *
     * @param questionId
     * @param loginUser
     * @return
     */
    @Override
    public int doQuestionFavour(long questionId, User loginUser) {
        // 判断是否存在
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已题目收藏
        long userId = loginUser.getId();
        // 每个用户串行题目收藏
        // 锁必须要包裹住事务方法
        QuestionFavourService questionFavourService = (QuestionFavourService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return questionFavourService.doQuestionFavourInner(userId, questionId);
        }
    }

    @Override
    public Page<QuestionFavourBO> listFavourQuestionByPage(IPage<QuestionFavourBO> page, long favourUserId) {
        if (favourUserId <= 0) {
            return new Page<>();
        }
        return questionFavourMapper.listFavourQuestionByPage(page, favourUserId);
    }

    @Override
    public Page<QuestionFavourVO> listMyFavourQuestionByPage(IPage<QuestionFavourVO> page, Wrapper<QuestionFavourVO> queryWrapper, long favourUserId) {
        return null;
    }

//    public Page<QuestionFavourVO> listMyFavourQuestionByPage(IPage<Question> page, Wrapper<Question> queryWrapper, long favourUserId) {
//        if (favourUserId <= 0) {
//            return new Page<>();
//        }
//        return baseMapper.listFavourQuestionByPage(page, favourUserId);
//    }

    /**
     * 封装了事务的方法
     *
     * @param userId
     * @param questionId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doQuestionFavourInner(long userId, long questionId) {
        QuestionFavour questionFavour = new QuestionFavour();
        questionFavour.setUserId(userId);
        questionFavour.setQuestionId(questionId);
        LambdaQueryWrapper<QuestionFavour> questionFavourQueryWrapper = new LambdaQueryWrapper<>(questionFavour);
        QuestionFavour oldQuestionFavour = this.getOne(questionFavourQueryWrapper);
        boolean result;
        // 已收藏
        if (oldQuestionFavour != null) {
            result = this.remove(questionFavourQueryWrapper);
            if (result) {
                // 题目收藏数 - 1
                result = questionService.update()
                        .eq("id", questionId)
                        .gt("favour_num", 0)
                        .setSql("favour_num = favour_num - 1")
                        .update();
                return result ? -1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        } else {
            // 未题目收藏
            result = this.save(questionFavour);
            if (result) {
                // 题目收藏数 + 1
                result = questionService.update()
                        .eq("id", questionId)
                        .setSql("favour_num = favour_num + 1")
                        .update();
                return result ? 1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
    }

    @Override
    public Page<QuestionFavourVO> getFavourQuestionVOPage(Page<QuestionFavourBO> favourQuestionPage, HttpServletRequest request) {
        List<QuestionFavourBO> questionList = favourQuestionPage.getRecords();
        Page<QuestionFavourVO> questionVOPage = new Page<>(favourQuestionPage.getCurrent(), favourQuestionPage.getSize(), favourQuestionPage.getTotal());
        if (CollectionUtils.isEmpty(questionList)) {
            return questionVOPage;
        }
        /*
            ● 创建题目人的信息*/
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(QuestionFavourBO::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
        Map<Long, Boolean> questionIdHasFavourMap = new HashMap<>();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            Set<Long> questionIdSet = questionList.stream().map(QuestionFavourBO::getQuestionId).collect(Collectors.toSet());
            loginUser = userService.getLoginUser(request);
            // 获取收藏
            LambdaQueryWrapper<QuestionFavour> questionFavourQueryWrapper = new LambdaQueryWrapper<>();
            questionFavourQueryWrapper.in(QuestionFavour::getQuestionId, questionIdSet);
            questionFavourQueryWrapper.eq(QuestionFavour::getUserId, loginUser.getId());
            List<QuestionFavour> questionFavourList = questionFavourMapper.selectList(questionFavourQueryWrapper);
            questionFavourList.forEach(questionFavour -> questionIdHasFavourMap.put(questionFavour.getQuestionId(), true));
        }
        // 填充信息
        List<QuestionFavourVO> questionVOList = questionList.stream().map(question -> {
            QuestionFavourVO questionVO = QuestionFavourVO.boToVo(question);
            Long userId = question.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUserVO(userService.getUserVO(user));
            questionVO.setHasFavour(questionIdHasFavourMap.getOrDefault(question.getQuestionId(), false));
            return questionVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }
}
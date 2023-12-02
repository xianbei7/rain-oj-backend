package com.rain.oj.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rain.oj.model.bo.QuestionFavourBO;
import com.rain.oj.model.entity.Question;
import com.rain.oj.model.entity.QuestionFavour;
import com.rain.oj.model.entity.User;
import com.rain.oj.model.vo.QuestionFavourVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 题目收藏服务
 */
public interface QuestionFavourService extends IService<QuestionFavour> {

    /**
     * 题目收藏
     *
     * @param questionId
     * @param loginUser
     * @return
     */
    int doQuestionFavour(long questionId, User loginUser);

    /**
     * 分页获取用户收藏的题目列表
     *
     * @param page
     * @param favourUserId
     * @return
     */
    Page<QuestionFavourBO> listFavourQuestionByPage(IPage<QuestionFavourBO> page, long favourUserId);

    /**
     * 分页获取用户自己收藏的题目列表
     *
     * @param page
     * @param queryWrapper
     * @param favourUserId
     * @return
     */
    Page<QuestionFavourVO> listMyFavourQuestionByPage(IPage<QuestionFavourVO> page, Wrapper<QuestionFavourVO> queryWrapper,
                                                      long favourUserId);

    /**
     * 题目收藏（内部服务）
     *
     * @param userId
     * @param questionId
     * @return
     */
    int doQuestionFavourInner(long userId, long questionId);

    Page<QuestionFavourVO> getFavourQuestionVOPage(Page<QuestionFavourBO> questionPage, HttpServletRequest request);
}

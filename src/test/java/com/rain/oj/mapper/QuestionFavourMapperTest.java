package com.rain.oj.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.annotation.Resource;

import com.rain.oj.model.entity.Question;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 题目收藏数据库操作测试
 *
 */
@SpringBootTest
class QuestionFavourMapperTest {

    @Resource
    private QuestionFavourMapper questionFavourMapper;

    @Test
    void listUserFavourQuestionByPage() {
        IPage<Question> page = new Page<>(2, 1);
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Question::getId, 1);
        queryWrapper.like(Question::getContent, "a");
        IPage<Question> result = questionFavourMapper.listFavourQuestionByPage(page, queryWrapper, 1);
        Assertions.assertNotNull(result);
    }
}
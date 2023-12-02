package com.rain.oj.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rain.oj.model.bo.QuestionFavourBO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

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
        IPage<QuestionFavourBO> page = new Page<>(1, 10);
        IPage<QuestionFavourBO> result = questionFavourMapper.listFavourQuestionByPage(page,1726913181595181058L);
        Assertions.assertNotNull(result);
    }
}
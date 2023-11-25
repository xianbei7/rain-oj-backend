package com.rain.oj.mapper;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;

import com.rain.oj.model.entity.Question;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 题目数据库操作测试
 *
 */
@SpringBootTest
class QuestionMapperTest {

    @Resource
    private QuestionMapper questionMapper;

    @Test
    void getByNumberTest() {
        Integer number = questionMapper.getByNumber(0);
        Assertions.assertNull(number);
    }
}
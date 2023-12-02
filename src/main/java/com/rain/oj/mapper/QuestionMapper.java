package com.rain.oj.mapper;

import com.rain.oj.model.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* 题目数据库操作
*/
public interface QuestionMapper extends BaseMapper<Question> {
    Integer getByNumber(Integer number);
}





package com.rain.oj.mapper;

import com.rain.oj.model.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* 对question表的数据库操作Mapper
*/
public interface QuestionMapper extends BaseMapper<Question> {
    Integer getByNumber(Integer number);
}





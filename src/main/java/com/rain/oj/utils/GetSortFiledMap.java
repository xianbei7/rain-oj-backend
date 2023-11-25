package com.rain.oj.utils;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.rain.oj.model.entity.Question;

import java.util.HashMap;
import java.util.Map;

public class GetSortFiledMap {
    Map<String, SFunction<Question,?>> map = new HashMap<>();
    {
        map.put("number",Question::getNumber);
        map.put("title",Question::getTitle);
        map.put("difficulty",Question::getDifficulty);
        map.put("submitNum",Question::getSubmitNum);
        map.put("acceptedNum",Question::getAcceptedNum);
        map.put("thumbNum",Question::getThumbNum);
        map.put("createTime",Question::getCreateTime);
        map.put("updateTime",Question::getUpdateTime);
    }
}

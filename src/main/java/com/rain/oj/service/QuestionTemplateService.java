package com.rain.oj.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.rain.oj.model.entity.QuestionTemplate;

/**
* 题目模板服务
*/
public interface QuestionTemplateService extends IService<QuestionTemplate> {
    String getCodeTemplate(Long questionId,String language);
}

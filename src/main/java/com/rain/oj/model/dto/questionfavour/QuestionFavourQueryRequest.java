package com.rain.oj.model.dto.questionfavour;

import com.rain.oj.common.PageRequest;
import com.rain.oj.model.dto.question.QuestionQueryRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 题目收藏查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionFavourQueryRequest extends PageRequest implements Serializable {

    /**
     * 用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}
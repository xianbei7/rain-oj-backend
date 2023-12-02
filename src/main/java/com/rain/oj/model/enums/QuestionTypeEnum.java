package com.rain.oj.model.enums;


import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum QuestionTypeEnum {

    ACM("acm", 1),
    CORE("core", 2),
    SQL("sql", 3);

    private final String type;

    private final Integer value;

    QuestionTypeEnum(String text, Integer value) {
        this.type = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 type 获取枚举
     *
     * @param type
     * @return
     */
    public static QuestionTypeEnum getEnumByType(String type) {
        if (StringUtils.isBlank(type)) {
            return null;
        }
        for (QuestionTypeEnum anEnum : QuestionTypeEnum.values()) {
            if (anEnum.type.equals(type)) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static QuestionTypeEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (QuestionTypeEnum anEnum : QuestionTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
}

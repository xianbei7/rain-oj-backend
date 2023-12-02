package com.rain.oj.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 编程语言枚举
 */
public enum ProgrammingLanguageEnum {

    C("c"),
    CPLUSPLUS("cpp"),
    JAVA("java"),
    GOLANG("go"),
    JAVASCRIPT("javascript");

    private final String text;


    ProgrammingLanguageEnum(String text) {
        this.text = text;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getLanguages() {
        return Arrays.stream(values()).map(item -> item.text).collect(Collectors.toList());
    }
    /**
     * 根据 text 获取枚举
     *
     * @param text
     * @return
     */
    public static ProgrammingLanguageEnum getEnumByText(String text) {
        if (ObjectUtils.isEmpty(text)) {
            return null;
        }
        for (ProgrammingLanguageEnum anEnum : ProgrammingLanguageEnum.values()) {
            if (anEnum.text.equals(text)) {
                return anEnum;
            }
        }
        return null;
    }
    public String getText() {
        return text;
    }
}

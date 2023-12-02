package com.rain.oj.model.enums;

import org.apache.commons.lang3.ObjectUtils;

/**
 * 用户角色枚举
 */
public enum CodeSandboxEnum {

    EXAMPLE("示例代码沙箱","example"),
    REMOTE("远程代码沙箱","remote"),
    THIRD_PARTY("第三方代码沙箱","thirdParty");
    private final String text;
    private final String value;

    CodeSandboxEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static CodeSandboxEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (CodeSandboxEnum anEnum : CodeSandboxEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}

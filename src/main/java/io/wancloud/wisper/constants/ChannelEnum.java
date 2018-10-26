package io.wancloud.wisper.constants;

import org.apache.commons.lang3.StringUtils;

/**
 * 通知渠道的定义
 */
public enum ChannelEnum {

    CHANNEL_MAIL("CHANNEL_MAIL", "mailSenderService", "邮件"),
    CHANNEL_SMS("CHANNEL_SMS", "smsSenderService", "短信"),;

    private String code;
    private String beanName;
    private String message;

    ChannelEnum(String code, String beanName, String message) {
        this.code = code;
        this.beanName = beanName;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getBeanName() {
        return beanName;
    }

    public String getMessage() {
        return message;
    }

    public static ChannelEnum findEnumByCode(String code) {
        for (ChannelEnum c : ChannelEnum.class.getEnumConstants()) {
            if (StringUtils.equals(c.getCode(), code)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.code;
    }

}

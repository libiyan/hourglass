package com.drgou.enums;

/**
 * 微信订阅消息模板类型
 */
public enum TemplateTypeEnums {

    UPGRADE_AGENT(1, "升级超级会员通知");

    private Integer code;
    private String msg;

    TemplateTypeEnums(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

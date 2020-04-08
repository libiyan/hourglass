package com.drgou.enums;

/**
 * 微信订阅消息 通知类型（状态）
 */
public enum ResultStatusEnums {

    FAILURE(0, "失败"),
    SUCCESS(1, "成功");

    private Integer code;
    private String msg;

    ResultStatusEnums(Integer code, String msg) {
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

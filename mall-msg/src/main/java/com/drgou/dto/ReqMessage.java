package com.drgou.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ReqMessage {
    /**
     * 接收者（用户）的 openid
     */
    private String touser;
    /**
     * 所需下发的模板消息的id
     */
    private String template_id;
    /**
     * 点击模板卡片后的跳转页面
     */
    private String page;
    /**
     * 模板消息
     */
    private Map data;

}

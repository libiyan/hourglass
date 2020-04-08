package com.drgou.constant;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: 微信小程序 消息订阅模板
 * @Author: wendy
 * @Date: 2020/3/20
 */
@Configuration
@Data
public class WxConfigConstant {

    /**
     * 微信APPID
     */
    @Value("${jdmall.wx.wxAppId}")
    private String wxAppId;

    /**
     * 微信密钥
     */
    @Value("${jdmall.wx.wxAppSecret}")
    private String wxAppSecret;
    /**
     * 升级超级会员通知
     */
    @Value("${jdmall.wx.upgrade.agent.template.id}")
    private String upgradeAgentTemplateId;
}

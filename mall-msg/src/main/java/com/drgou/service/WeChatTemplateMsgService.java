package com.drgou.service;


import com.drgou.utils.JsonResult;

/**
 * @Description: 微信小程序 订阅模板消息
 * @Author: wendy
 * @Date: 2020/3/19
 */
public interface WeChatTemplateMsgService {
    /**
     * 检测是否订阅
     *
     * @param userId
     * @param templateId
     * @return
     */
    JsonResult checkSubscription(String userId, String templateId);

    /**
     * 保存订阅信息
     *
     * @param userId
     * @param wxOpenId
     * @param mobile
     * @param templateId
     * @return
     */
    JsonResult saveSubscription(Long userId, String wxOpenId, Long mobile, String templateId);

    /**
     * 发送订阅消息
     *
     * @param userId
     * @param templateType
     * @return
     */
    JsonResult sendSubscription(String userId, Integer templateType, Integer status);

    /**
     * 失败重推
     *
     * @param templateType
     * @return
     */
    JsonResult reSend(Integer templateType);
}

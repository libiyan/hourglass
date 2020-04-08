package com.drgou.controller;

import com.alibaba.fastjson.JSONObject;
import com.drgou.dto.JdUserInfoDTO;
import com.drgou.enums.TemplateTypeEnums;
import com.drgou.interceptor.TokenValidate;
import com.drgou.service.UserService;
import com.drgou.service.WeChatTemplateMsgService;
import com.drgou.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 微信小程序 订阅消息推送相关
 * @Author: wendy
 * @Date: 2020/3/19
 */
@RestController
@RequestMapping("/msg/wechatTemplateMsg")
public class WeChatTemplateMsgController {

    @Autowired
    private UserService userService;
    @Autowired
    private WeChatTemplateMsgService weChatTemplateMsgService;

    /**
     * 检测是否已定阅消息
     *
     * @param token
     * @param templateId 模板id
     * @return
     */
    @TokenValidate
    @PostMapping("/checkSubscription")
    public JsonResult checkSubscription(String token, String templateId) {
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(templateId)) {
            return JsonResult.build(JsonResult.STATUS_SERVER_EXCEPTION, "请求数据错误");
        }
        JsonResult jsonResult = userService.getJdUserBaseInfoByToken(token);
        if (jsonResult.getStatus() == 200) {
            JdUserInfoDTO jdUserInfo = JSONObject.parseObject(JSONObject.toJSONString(jsonResult.getData()), JdUserInfoDTO.class);
            return weChatTemplateMsgService.checkSubscription(jdUserInfo.getUserId().toString(), templateId);
        } else {
            return jsonResult;
        }
    }

    /**
     * 保存微信订阅消息
     *
     * @param token
     * @return
     */
    @TokenValidate
    @PostMapping("/saveSubscription")
    public JsonResult saveSubscription(String token, String templateId) {
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(templateId)) {
            return JsonResult.build(JsonResult.STATUS_SERVER_EXCEPTION, "请求数据错误");
        }
        JsonResult jsonResult = userService.getJdUserBaseInfoByToken(token);
        if (jsonResult.getStatus() == 200) {
            JdUserInfoDTO jdUserInfo = JSONObject.parseObject(JSONObject.toJSONString(jsonResult.getData()), JdUserInfoDTO.class);
            return weChatTemplateMsgService.saveSubscription(jdUserInfo.getUserId(), jdUserInfo.getWxOpenId(), jdUserInfo.getMobile(), templateId);
        } else {
            return jsonResult;
        }
    }

    /**
     * 发送微信订阅消息
     *
     * @param userId
     * @param templateType 1 升级通知
     * @return
     */
    @PostMapping("/sendSubscription")
    public JsonResult sendSubscription(String userId, Integer templateType, Integer status) {
        try {
            if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(templateType)) {
                return JsonResult.build(JsonResult.STATUS_SERVER_EXCEPTION, "请求数据错误");
            }
            return weChatTemplateMsgService.sendSubscription(userId, templateType, status);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(JsonResult.STATUS_SERVER_EXCEPTION, "服务异常");
        }
    }

    @RequestMapping("/reSend")
    public JsonResult reSend(Integer templateType) {
        if (StringUtils.isEmpty(templateType)) {
            templateType = TemplateTypeEnums.UPGRADE_AGENT.getCode();
        }
        JsonResult jsonResult = weChatTemplateMsgService.reSend(templateType);
        if (JsonResult.STATUS_OK != jsonResult.getStatus()) {
            return jsonResult;
        }
        return JsonResult.ok("消息发送完成!-msg=" + jsonResult.getMsg() + "-status=" + jsonResult.getStatus());
    }
}

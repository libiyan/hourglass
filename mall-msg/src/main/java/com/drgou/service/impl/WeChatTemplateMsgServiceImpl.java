package com.drgou.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.drgou.constant.WxConfigConstant;
import com.drgou.dto.ReqMessage;
import com.drgou.enums.ResultStatusEnums;
import com.drgou.enums.TemplateTypeEnums;
import com.drgou.service.RedisUtilService;
import com.drgou.service.SendMessageService;
import com.drgou.service.WeChatTemplateMsgService;
import com.drgou.task.WeChatSendMsgTaskManager;
import com.drgou.utils.DateUtils;
import com.drgou.utils.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class WeChatTemplateMsgServiceImpl implements WeChatTemplateMsgService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisUtilService redisUtilService;
    @Autowired
    private WxConfigConstant wxConfigConstant;
    @Autowired
    private SendMessageService sendMessageService;
    @Autowired
    private WeChatSendMsgTaskManager weChatSendMsgTaskManager;

    /**
     * 订阅微信提醒消息
     */
    private static final String WeChat_Msg_Subscription = "Jdmall_WeChat_TemplateId:";
    private static final String WeChat_Msg_Subscription_Send_Fail = "Jdmall_WeChat_SendFail_TemplateId:";

    @Override
    public JsonResult checkSubscription(String userId, String templateId) {
        Object result = redisUtilService.get(WeChat_Msg_Subscription + templateId + ":userId:" + userId);
        if (null != result) {
            return JsonResult.ok(1);
        } else {
            return JsonResult.ok(0);
        }
    }

    @Override
    public JsonResult saveSubscription(Long userId, String wxOpenId, Long mobile, String templateId) {
        String userKey = WeChat_Msg_Subscription + templateId + ":userId:" + userId;
        try {
            if (null == redisUtilService.get(userKey)) {
                redisUtilService.set(userKey, wxOpenId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            redisUtilService.remove(userKey);
            return JsonResult.build(JsonResult.STATUS_SERVER_EXCEPTION, "服务员异常，订阅失败");
        }
        return JsonResult.ok();
    }

    @Override
    public JsonResult sendSubscription(String userId, Integer templateType, Integer status) {
        JsonResult sendResult = sendMessageService.getAccessToken();
        if (JsonResult.STATUS_OK != sendResult.getStatus() && null == sendResult.getData()) {
            return sendResult;
        }
        String templateId = "";
        if (TemplateTypeEnums.UPGRADE_AGENT.getCode().equals(templateType)) {
            templateId = wxConfigConstant.getUpgradeAgentTemplateId();
        }
        if (StringUtils.isEmpty(templateId)) {
            return JsonResult.build(JsonResult.STATUS_SERVER_EXCEPTION, "微信小程序templateId不能为空！");
        } else {
            String key = WeChat_Msg_Subscription + templateId + ":userId:" + userId;
            Object wxOpenId = redisUtilService.get(key);
            if (Objects.isNull(wxOpenId)) {
                return JsonResult.build(JsonResult.STATUS_SERVER_EXCEPTION, "用户wxOpenId不能为空！");
            } else {
                logger.info("发送小程序" + TemplateTypeEnums.values()[templateType - 1].getMsg() + "模版消息:{}", "key=" + key + "openId=" + wxOpenId);
                ReqMessage reqMessage = getReqMessage(templateType, String.valueOf(wxOpenId), status);
                weChatSendMsgTaskManager.put(() -> sendTemplateMsg(sendResult, userId, templateType, key, reqMessage));
                return sendResult;
            }
        }
    }

    /**
     * @param sendResult
     * @param userId
     * @param templateType 1超级会员升级推送
     * @param key
     * @param reqMessage
     */
    private void sendTemplateMsg(JsonResult sendResult, String userId, Integer templateType, String key, ReqMessage reqMessage) {
        JsonResult sendMsgResult = sendMessageService.sendMessage(reqMessage);
        if (sendMsgResult.getStatus() == JsonResult.STATUS_OK) {
            redisUtilService.remove(key);
            logger.info("userId=" + userId + "-openId=" + reqMessage.getTouser() + "-消息发送成功" + "-status=" + sendMsgResult.getStatus() + "-msg=" + sendMsgResult.getMsg());
        } else {
            if ("获取微信token失败".equals(sendMsgResult.getMsg())) {
                sendResult.setStatus(sendMsgResult.getStatus());
                sendResult.setMsg(sendMsgResult.getMsg());
            } else {
                if (40003 == sendMsgResult.getStatus() || 43101 == sendMsgResult.getStatus()) {

                } else {
                    redisUtilService.SADD(WeChat_Msg_Subscription_Send_Fail + templateType, JSONObject.toJSONString(reqMessage));
                }
                redisUtilService.remove(key);
                logger.error("userId=" + userId + "-openId=" + reqMessage.getTouser() + "-消息发送失败" + "-status=" + sendMsgResult.getStatus() + "-msg=" + sendMsgResult.getMsg());
            }
        }
    }

    /**
     * 模版拼装-超级会员升级通知
     *
     * @param templateType 模板类型
     * @param wxOpenId     用户openId
     * @return
     */
    private ReqMessage getReqMessage(Integer templateType, String wxOpenId, Integer status) {
        ReqMessage reqMessage = new ReqMessage();
        Map messageData = new HashMap();
        if (TemplateTypeEnums.UPGRADE_AGENT.getCode().equals(templateType)) {
            Map phrase1 = new HashMap();
            phrase1.put("value", "会员升级");
            messageData.put("phrase1", phrase1);
            Map time2 = new HashMap();
            time2.put("value", DateUtils.getNowTime(DateUtils.DATE_FORMAT_DATETIME));
            messageData.put("time2", time2);
            Map name3 = new HashMap();
            if (StringUtils.isEmpty(status)) {
                name3.put("value", "超级会员升级中");
            } else {
                if (ResultStatusEnums.FAILURE.getCode().equals(status)) {
                    name3.put("value", "超级会员升级失败");
                }
                if (ResultStatusEnums.SUCCESS.getCode().equals(status)) {
                    name3.put("value", "超级会员升级成功");
                }
            }
            messageData.put("name3", name3);
            reqMessage.setPage("/pages/personCenter/index");
            reqMessage.setTouser(wxOpenId);
            reqMessage.setTemplate_id(wxConfigConstant.getUpgradeAgentTemplateId());
        }
        reqMessage.setData(messageData);
        return reqMessage;
    }

    @Override
    public JsonResult reSend(Integer templateType) {
        JsonResult sendResult = sendMessageService.getAccessToken();
        if (JsonResult.STATUS_OK != sendResult.getStatus()) {
            return sendResult;
        }
        boolean hasValue = true;
        while (hasValue) {
            Object resultStr = redisUtilService.SPOP(WeChat_Msg_Subscription_Send_Fail + templateType);
            if (null == resultStr) {
                hasValue = false;
            } else {
                logger.info("发送小程序" + TemplateTypeEnums.values()[templateType - 1].getMsg() + "模版消息:{}", resultStr);
                ReqMessage reqMessage = JSONObject.parseObject((String) resultStr, ReqMessage.class);
                if (null != reqMessage) {
                    weChatSendMsgTaskManager.put(() -> reSendMsg(templateType, sendResult, reqMessage));
                } else {
                    continue;
                }
            }
        }
        logger.info("发送小程序" + TemplateTypeEnums.values()[templateType - 1].getMsg() + "模版消息完成");
        return sendResult;
    }

    private void reSendMsg(Integer templateType, JsonResult sendResult, ReqMessage reqMessage) {
        JsonResult sendMsgResult = sendMessageService.sendMessage(reqMessage);
        if (sendMsgResult.getStatus() == JsonResult.STATUS_OK) {
            logger.info("openId=" + reqMessage.getTouser() + "-消息发送成功" + "-status=" + sendMsgResult.getStatus() + "-msg=" + sendMsgResult.getMsg());
        } else {
            if ("获取微信token失败".equals(sendMsgResult.getMsg())) {
                redisUtilService.SADD(WeChat_Msg_Subscription_Send_Fail + templateType, reqMessage);
                sendResult.setStatus(sendMsgResult.getStatus());
                sendResult.setMsg(sendMsgResult.getMsg());
            } else {
                logger.error("openId=" + reqMessage.getTouser() + "-消息发送失败" + "-status=" + sendMsgResult.getStatus() + "-msg=" + sendMsgResult.getMsg());
            }
        }
    }
}

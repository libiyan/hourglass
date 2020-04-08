package com.drgou.service;

import com.alibaba.fastjson.JSONObject;
import com.drgou.config.feign.WxFeignClient;
import com.drgou.dto.ReqMessage;
import com.drgou.utils.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Service
public class SendMessageService extends RestRequestService {

    private Logger logger = LoggerFactory.getLogger(SendMessageService.class);

    @Resource
    private WxFeignClient wxFeignClient;

    @Autowired
    private RedisUtilService redisUtilService;

    public JsonResult getAccessToken() {
        String cacheAccessTokenKey = "Mall_Access_Token_New:";
        String accessToken = (String) redisUtilService.get(cacheAccessTokenKey);
        if (StringUtils.isEmpty(accessToken)) {
            return JsonResult.build(JsonResult.STATUS_SERVER_EXCEPTION, "获取微信token失败");
        } else {
            return JsonResult.ok(accessToken);
        }
    }

    /**
     * 推送微信小程序订阅消息
     * 接口文档地址：https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/subscribe-message/subscribeMessage.send.html
     *
     * @param reqMessage
     * @return errcode
     * 40003    touser字段openid为空或者不正确
     * 40037    订阅模板id为空不正确
     * 43101	用户拒绝接受消息，如果用户之前曾经订阅过，则表示用户取消了订阅关系
     * 47003	模板参数不准确，可能为空或者不满足规则，errmsg会提示具体是哪个字段出错
     * 41030	page路径不正确，需要保证在现网版本小程序中存在，与app.json保持一致
     * 接口限制 次数限制：开通支付能力的是3kw/日，没开通的是1kw/日。
     */
    public JsonResult sendMessage(ReqMessage reqMessage) {
        JsonResult jsonResult = getAccessToken();
        if (jsonResult.getStatus() == JsonResult.STATUS_OK) {
            String accessToken = (String) jsonResult.getData();
            JSONObject jsonObject = wxFeignClient.sendMessage(accessToken, reqMessage);
            if (jsonObject != null && jsonObject.containsKey("errcode")) {
                Integer errcode = jsonObject.getInteger("errcode");
                String errmsg = jsonObject.getString("errmsg");
                if (errcode == 0) {
                    logger.info("openId=" + reqMessage.getTouser() + "-消息发送成功");
                    return JsonResult.ok();
                } else {
                    logger.error("openId=" + reqMessage.getTouser() + "消息发送失败" + "-errcode=" + errcode + "-errmsg=" + errmsg);
                    return JsonResult.build(errcode, errmsg);
                }
            }
        }
        return jsonResult;
    }

}

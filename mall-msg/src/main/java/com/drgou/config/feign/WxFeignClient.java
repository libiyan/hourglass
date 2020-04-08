package com.drgou.config.feign;

import com.alibaba.fastjson.JSONObject;
import com.drgou.dto.ReqMessage;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "wx-client", url = "https://api.weixin.qq.com")
public interface WxFeignClient {

    /**
     * 微信小程序订阅消息
     *
     * @param access_token
     * @param reqMessage
     * @return
     */
    @PostMapping(value = "/cgi-bin/message/subscribe/send")
    JSONObject sendMessage(@RequestParam(name = "access_token") String access_token, @RequestBody ReqMessage reqMessage);
}

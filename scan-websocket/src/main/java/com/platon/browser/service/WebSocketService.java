package com.platon.browser.service;

import com.platon.browser.util.JsonUtil;
import com.platon.browser.websocket.Params;
import com.platon.browser.websocket.Request;
import com.platon.browser.websocket.SubscriptionResponse;
import com.platon.browser.websocket.WebSocketData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WebSocketService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void send(WebSocketData webSocketData, Object result) {
        Request request = webSocketData.getRequest();

        SubscriptionResponse<Object> response = new SubscriptionResponse<>();
        response.setJsonrpc(request.getJsonrpc());
        response.setMethod("eth_subscription");
        Params<Object> params = new Params<>();
        params.setSubscription(webSocketData.getRequestHash());
        params.setResult(result);
        response.setParams(params);

        webSocketData.setResponse(response);
        String message = JsonUtil.toJson(webSocketData);
        redisTemplate.convertAndSend(webSocketData.getResponseChannel(), message);
    }
}

package com.platon.browser.service;

import com.platon.browser.config.RedisKeyConfig;
import com.platon.browser.util.JsonUtil;
import com.platon.browser.websocket.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class WebSocketService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private RedisKeyConfig redisKeyConfig;
    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private NewHeadsService newHeadsService;
    private static final Map<Session, WebSocketData> MAP = new ConcurrentHashMap<>();

    public static WebSocketData get(Session session) {
        return MAP.get(session);
    }
    public static Map<Session, WebSocketData> getMap() {
        return MAP;
    }

    public void sub(WebSocketChannelData webSocketChannelData) {
    }

    public void send(WebSocketChannelData webSocketData, Object result) {
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

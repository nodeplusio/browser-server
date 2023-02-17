package com.platon.browser.service;

import com.platon.browser.util.JsonUtil;
import com.platon.browser.websocket.Params;
import com.platon.browser.websocket.Request;
import com.platon.browser.websocket.SubscriptionResponse;
import com.platon.browser.websocket.WebSocketData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class WebSocketService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ExecutorService sendExecutorService;
    @Value("${ws.sendExecutorSize:20}")
    private int sendExecutorThreadSize;

    @Bean
    ExecutorService sendExecutorService() {
        return Executors.newFixedThreadPool(sendExecutorThreadSize);
    }

    public void send(WebSocketData webSocketData, Object result) {
        sendExecutorService.submit(() -> {
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
        });
    }
}

package com.platon.browser.service;

import com.platon.browser.util.JsonUtil;
import com.platon.browser.websocket.WebSocketResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class AbstractSubscriptionService implements SubscriptionService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    protected List<WebSocketResponse> responses = new ArrayList<>();
    protected String responseChannel;

    @Override
    public void send() {
        log.debug("send count: {}", responses.size());
        String message = JsonUtil.toJson(responses);
        redisTemplate.convertAndSend(responseChannel, message);
    }

}

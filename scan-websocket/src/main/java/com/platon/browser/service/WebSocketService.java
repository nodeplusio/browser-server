package com.platon.browser.service;

import com.platon.browser.config.RedisKeyConfig;
import com.platon.browser.util.JsonUtil;
import com.platon.browser.websocket.Params;
import com.platon.browser.websocket.Request;
import com.platon.browser.websocket.SubscriptionResponse;
import com.platon.browser.websocket.WebSocketData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class WebSocketService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Value("${ws.sendExecutorSize:20}")
    private int sendExecutorThreadSize;
    @Value("${ws.subscribeExecutorSize:20}")
    private int subscribeExecutorThreadSize;
    @Value("${ws.batchExecutorThreadSize:20}")
    private int batchExecutorThreadSize;
    @Autowired
    private ExecutorService sendExecutorService;
    @Resource
    private RedisKeyConfig redisKeyConfig;
    @Value("${server.port}")
    private Integer port;
    @Getter
    private String pushDataKey;

    @PostConstruct
    public void init() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            String ipPort = ":" + address.getHostAddress() + ":" + port;
            pushDataKey = redisKeyConfig.getPushData() + ipPort;
        } catch (UnknownHostException e) {
            log.error("获取ip错误", e);
            pushDataKey = redisKeyConfig.getPushData();
        }
    }

    @Bean
    ExecutorService sendExecutorService() {
        return Executors.newFixedThreadPool(sendExecutorThreadSize);
    }

    @Bean
    ExecutorService subscribeExecutorService() {
        return Executors.newFixedThreadPool(subscribeExecutorThreadSize);
    }

    @Bean
    ExecutorService batchExecutorService() {
        return Executors.newFixedThreadPool(batchExecutorThreadSize);
    }

    public void send(WebSocketData webSocketData, Object result) {
        sendExecutorService.submit(() -> {
            long s = System.currentTimeMillis();
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
            HashOperations<String, Object, Object> operations = redisTemplate.opsForHash();
            operations.put(pushDataKey, webSocketData.getRequestHash(), message);
            redisTemplate.convertAndSend(webSocketData.getResponseChannel(), message);
            log.debug("webSocketService.send 耗时:{} ms", System.currentTimeMillis() - s);
        });
    }
}

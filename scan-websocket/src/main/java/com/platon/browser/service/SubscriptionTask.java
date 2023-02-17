package com.platon.browser.service;

import com.alibaba.fastjson.JSON;
import com.platon.browser.config.RedisKeyConfig;
import com.platon.browser.util.JsonUtil;
import com.platon.browser.websocket.Request;
import com.platon.browser.websocket.WebSocketData;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class SubscriptionTask {
    private static final String ETH_UNSUBSCRIBE = "eth_unsubscribe";

    @Resource
    private ApplicationContext applicationContext;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private RedisKeyConfig redisKeyConfig;
    @Resource
    private RedissonClient redissonClient;
    private static final ExecutorService SUBSCRIBE_EXECUTOR = Executors.newSingleThreadExecutor();
    @Value("${server.port}")
    private Integer port;
    @Value("${ws-send.delay-ms:30000}")
    private long delayMs;

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

    /**
     * 订阅Subscription
     */
    @PostConstruct
    void subscribe() {
        SUBSCRIBE_EXECUTOR.submit(() -> {
            ListOperations<String, String> listOperations = redisTemplate.opsForList();
            while (true) {
                log.debug("订阅Subscription");
                String s = listOperations.rightPop(redisKeyConfig.getProxyRequestChannel(), Duration.ofSeconds(1));
                if (s == null) {
                    continue;
                }
                WebSocketData webSocketData = JSON.parseObject(s, WebSocketData.class);
                Request request = webSocketData.getRequest();
                HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
                if (ETH_UNSUBSCRIBE.equals(request.getMethod())) {
                    hashOperations.delete(pushDataKey, webSocketData.getRequestHash());
                } else {
                    webSocketData.setDataTime(System.currentTimeMillis());
                    hashOperations.put(pushDataKey, webSocketData.getRequestHash(), JsonUtil.toJson(webSocketData));
                }
            }
        });
    }

    /**
     * 容器关闭时转移推送请求
     */
    @Bean
    ApplicationListener<ContextClosedEvent> contextClosedEventApplicationListener() {
        return event -> {
            SUBSCRIBE_EXECUTOR.shutdownNow();
            ListOperations<String, String> listOperations = redisTemplate.opsForList();
            HashOperations<String, String, String> operations = redisTemplate.opsForHash();
            Map<String, String> entries = operations.entries(pushDataKey);
            log.debug("需要转移的请求数量{}", entries.size());
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                listOperations.leftPush(redisKeyConfig.getProxyRequestChannel(), entry.getValue());
                operations.delete(pushDataKey, entry.getKey());
            }
        };
    }

    /**
     * 监控推送
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void monitor() {
        RLock lock = redissonClient.getLock("PushDelayMonitor");
        try {
            ListOperations<String, String> listOperations = redisTemplate.opsForList();
            HashOperations<String, String, String> operations = redisTemplate.opsForHash();
            Set<String> keys = redisTemplate.keys(redisKeyConfig.getPushData() + "*");
            if (keys == null) {
                return;
            }
            for (String key : keys) {
                if (!key.equals(pushDataKey)) {
                    for (String key1 : operations.keys(key)) {
                        String s = operations.get(key, key1);
                        WebSocketData webSocketData = JSON.parseObject(s, WebSocketData.class);
                        if (webSocketData == null) {
                            continue;
                        }
                        if (System.currentTimeMillis() - webSocketData.getDataTime() > delayMs) {
                            log.debug("重新推送的key:{}", key1);
                            operations.delete(key, key1);
                            listOperations.leftPush(redisKeyConfig.getProxyRequestChannel(), s);
                        }
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 推送Subscription
     */
    @Scheduled(cron = "0/1 * * * * ?")
    public void send() {
        long dataTime = System.currentTimeMillis();
        HashOperations<String, String, String> operations = redisTemplate.opsForHash();
        Map<String, SubscriptionService> serviceMap = new HashMap<>();
        for (String key : operations.keys(pushDataKey)) {
            try {
                WebSocketData webSocketData = JSON.parseObject(operations.get(pushDataKey, key), WebSocketData.class);
                if (webSocketData == null) {
                    continue;
                }
                Request request = webSocketData.getRequest();
                Object subscriptionType = request.getParams().get(0);
                String name = subscriptionType + "Service";
                if (applicationContext.containsBean(name)) {
                    SubscriptionService service;
                    if (serviceMap.containsKey(name)) {
                        service = serviceMap.get(name);
                    } else {
                        service = applicationContext.getBean(name, SubscriptionService.class);
                        serviceMap.put(name, service);
                    }
                    long s = System.currentTimeMillis();
                    service.subscribe(webSocketData);
                    log.debug("推送单个Subscription耗时:{} ms", System.currentTimeMillis() - s);
                    webSocketData.setDataTime(dataTime);
                    operations.put(pushDataKey, key, JsonUtil.toJson(webSocketData));
                }
            } catch (Exception e) {
                log.error("推送订阅信息失败", e);
            }
        }
        log.debug("推送全部Subscription耗时:{} ms", System.currentTimeMillis() - dataTime);
    }

}

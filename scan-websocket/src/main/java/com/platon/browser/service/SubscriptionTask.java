package com.platon.browser.service;

import com.alibaba.fastjson.JSON;
import com.platon.browser.config.RedisKeyConfig;
import com.platon.browser.websocket.Request;
import com.platon.browser.websocket.WebSocketChannelData;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;

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

    /**
     * 订阅Subscription
     */
    @Scheduled(cron = "0/1 * * * * ?")
    public void subscribe() {
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        while (true){
            log.debug("订阅Subscription");
            String s = listOperations.rightPop(redisKeyConfig.getProxyRequestChannel(), Duration.ofSeconds(1));
            if (s == null) {
                continue;
            }
            send(s);
        }
    }

    /**
     * 推送Subscription
     */
    @Scheduled(cron = "0/1 * * * * ?")
    public void send() {
        HashOperations<String, String, String> operations = redisTemplate.opsForHash();
        for (Object key : operations.keys(redisKeyConfig.getPushData())) {
            RLock pushDataLock = redissonClient.getLock("PushDataLock");
            try {
                pushDataLock.lock();
                String s = operations.get(redisKeyConfig.getPushData(), key);
                send(s);
            } catch (Exception e) {
                log.error("推送{}数据失败", key, e);
            } finally {
                pushDataLock.unlock();
            }
        }
    }

    private void send(String s) {
        WebSocketChannelData webSocketChannelData = JSON.parseObject(s, WebSocketChannelData.class);
        Request request = webSocketChannelData.getRequest();
        if (ETH_UNSUBSCRIBE.equals(request.getMethod())) {
            HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
            hashOperations.delete(redisKeyConfig.getPushData(), webSocketChannelData.getRequestHash());
            return;
        }
        Object subscriptionType = request.getParams().get(0);
        String name = subscriptionType + "Service";
        if (applicationContext.containsBean(name)) {
            try {
                applicationContext.getBean(name, SubscriptionService.class)
                        .subscribe(webSocketChannelData);
            } catch (Exception e) {
                log.error("推送订阅信息失败", e);
            }
        }
    }

}

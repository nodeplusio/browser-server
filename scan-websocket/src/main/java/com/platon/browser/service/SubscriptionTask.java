package com.platon.browser.service;

import com.platon.browser.websocket.Request;
import com.platon.browser.websocket.WebSocketData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.Map;

@Component
@Slf4j
public class SubscriptionTask {

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 推送Subscription
     */
    @Scheduled(cron = "0/1 * * * * ?")
    public synchronized void subscribe() {
        Map<Session, WebSocketData> map = WebSocketService.getMap();
        for (Map.Entry<Session, WebSocketData> entry : map.entrySet()) {
            for (Map.Entry<String, Request> request : entry.getValue().getRequests().entrySet()) {
                Object subscriptionType = request.getValue().getParams().get(0);
                applicationContext.getBean(subscriptionType + "Service", SubscriptionService.class)
                        .subscribe(entry, request);
            }
        }
    }

}

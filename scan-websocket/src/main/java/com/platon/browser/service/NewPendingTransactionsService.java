package com.platon.browser.service;

import com.alibaba.fastjson.JSON;
import com.platon.browser.config.RedisKeyConfig;
import com.platon.browser.service.tx.PendingTxQueryService;
import com.platon.browser.websocket.WebSocketChannelData;
import com.platon.protocol.core.methods.response.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class NewPendingTransactionsService implements SubscriptionService {

    @Resource
    private PendingTxQueryService pendingTxQueryService;

    @Resource
    private RedisKeyConfig redisKeyConfig;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private WebSocketService webSocketService;

    @Override
    public void subscribe(WebSocketChannelData webSocketData) {
        List<Transaction> transactions = pendingTxQueryService.queryPendingTxList();
        List<String> hashes = new ArrayList<>();
        for (Transaction transaction : transactions) {
            String hash = transaction.getHash();
            hashes.add(hash);
            if (webSocketData.getHashes().contains(hash)) {
                continue;
            }
            webSocketService.send(webSocketData, hash);
        }
        webSocketData.setHashes(hashes);
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(redisKeyConfig.getPushData(), webSocketData.getRequestHash(), JSON.toJSONString(webSocketData));
    }

}

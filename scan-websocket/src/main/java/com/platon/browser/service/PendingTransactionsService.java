package com.platon.browser.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.platon.bech32.Bech32;
import com.platon.browser.config.RedisKeyConfig;
import com.platon.browser.service.tx.PendingTxQueryService;
import com.platon.browser.websocket.WebSocketChannelData;
import com.platon.parameters.NetworkParameters;
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
public class PendingTransactionsService implements SubscriptionService {

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
        List<Object> requestParam = webSocketData.getRequest().getParams();
        List<String> fromAddressList = new ArrayList<>();
        List<String> toAddressList = new ArrayList<>();
        boolean hashesOnly = false;
        if (requestParam.size() > 1) {
            JSONObject param = (JSONObject) requestParam.get(1);
            hashesOnly = param.getBooleanValue("hashesOnly");
            Object fromAddress = param.get("fromAddress");
            if (fromAddress instanceof String) {
                fromAddressList.add((String) fromAddress);
            } else if (fromAddress instanceof List) {
                fromAddressList.addAll((List<String>) fromAddress);
            }
            Object toAddress = param.get("toAddress");
            if (toAddress instanceof String) {
                toAddressList.add((String) toAddress);
            } else if (toAddress instanceof List) {
                toAddressList.addAll((List<String>) toAddress);
            }
        }
        List<Transaction> transactions = pendingTxQueryService.queryPendingTxList();
        List<String> hashes = new ArrayList<>();
        NetworkParameters.selectPlatON();
        for (Transaction transaction : transactions) {
            String hash = transaction.getHash();
            hashes.add(hash);
            if (webSocketData.getHashes().contains(hash)
                    || !fromAddressList.isEmpty() && !fromAddressList.contains(Bech32.addressDecodeHex(transaction.getFrom()))
                    || !toAddressList.isEmpty() && !toAddressList.contains(Bech32.addressDecodeHex(transaction.getTo()))) {
                continue;
            }

            if (hashesOnly) {
                webSocketService.send(webSocketData, hash);
            } else {
                webSocketService.send(webSocketData, new TransactionResult(transaction));
            }
        }
        webSocketData.setHashes(hashes);
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(redisKeyConfig.getPushData(), webSocketData.getRequestHash(), JSON.toJSONString(webSocketData));
    }

}

package com.platon.browser.service;

import com.alibaba.fastjson.JSON;
import com.platon.browser.service.tx.PendingTxQueryService;
import com.platon.browser.websocket.Params;
import com.platon.browser.websocket.Request;
import com.platon.browser.websocket.SubscriptionResponse;
import com.platon.browser.websocket.WebSocketData;
import com.platon.protocol.core.methods.response.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class NewPendingTransactionsService implements SubscriptionService {

    @Resource
    private PendingTxQueryService pendingTxQueryService;

    @Override
    public void push(Map.Entry<Session, WebSocketData> entry, Map.Entry<String, Request> request) {
        List<Transaction> transactions = pendingTxQueryService.queryPendingTxList();
        List<String> hashes = new ArrayList<>();
        WebSocketData webSocketData = entry.getValue();
        System.out.println(webSocketData.getHashes());
        for (Transaction transaction : transactions) {
            String hash = transaction.getHash();
            System.out.println(hash);
            hashes.add(hash);
            if (webSocketData.getHashes().contains(hash)) {
                continue;
            }
            SubscriptionResponse<String> response = new SubscriptionResponse<>();
            response.setJsonrpc(request.getValue().getJsonrpc());
            response.setMethod("eth_subscription");
            Params<String> params = new Params<>();
            params.setSubscription(request.getKey());
            params.setResult(hash);
            response.setParams(params);
            RemoteEndpoint.Async asyncRemote = entry.getKey().getAsyncRemote();
            asyncRemote.sendText(JSON.toJSONString(response));
        }
        webSocketData.setHashes(hashes);
    }

}

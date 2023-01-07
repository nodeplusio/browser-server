package com.platon.browser.service;

import com.platon.browser.service.tx.PendingTxQueryService;
import com.platon.browser.websocket.Request;
import com.platon.browser.websocket.WebSocketData;
import com.platon.protocol.core.methods.response.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    public void subscribe(Map.Entry<Session, WebSocketData> entry, Map.Entry<String, Request> request) {
        List<Transaction> transactions = pendingTxQueryService.queryPendingTxList();
        List<String> hashes = new ArrayList<>();
        WebSocketData webSocketData = entry.getValue();
        for (Transaction transaction : transactions) {
            String hash = transaction.getHash();
            hashes.add(hash);
            if (webSocketData.getHashes().contains(hash)) {
                continue;
            }
            send(entry, request, hash);
        }
        webSocketData.setHashes(hashes);
    }

}

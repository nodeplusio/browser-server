package com.platon.browser.service;

import com.platon.browser.service.tx.PendingTxQueryService;
import com.platon.browser.util.JsonUtil;
import com.platon.browser.websocket.WebSocketData;
import com.platon.protocol.core.methods.response.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Scope("prototype")
@Slf4j
public class NewPendingTransactionsService implements SubscriptionService {

    @Resource
    private PendingTxQueryService pendingTxQueryService;
    @Resource
    private WebSocketService webSocketService;
    private List<Transaction> transactions;

    @PostConstruct
    void init() {
        transactions = pendingTxQueryService.queryPendingTxList();
    }

    @Override
    public void subscribe(WebSocketData webSocketData) {
        List<String> hashes = new ArrayList<>();
        String lastPushData = webSocketData.getLastPushData();
        for (Transaction transaction : transactions) {
            String hash = transaction.getHash();
            hashes.add(hash);
            if (lastPushData != null && lastPushData.contains(hash)) {
                continue;
            }
            webSocketService.send(webSocketData, hash);
        }
        webSocketData.setLastPushData(JsonUtil.toJson(hashes));
    }

}

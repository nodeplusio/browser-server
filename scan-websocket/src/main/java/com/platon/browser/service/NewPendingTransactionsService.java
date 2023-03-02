package com.platon.browser.service;

import com.platon.browser.service.tx.PendingTxQueryService;
import com.platon.browser.websocket.WebSocketData;
import com.platon.browser.websocket.WebSocketResponse;
import com.platon.protocol.core.methods.response.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Scope("prototype")
@Slf4j
public class NewPendingTransactionsService extends AbstractSubscriptionService {

    @Resource
    private PendingTxQueryService pendingTxQueryService;
    private List<Transaction> transactions;
    private static List<String> preTransactionHashes = new ArrayList<>();
    private List<String> hashes = new ArrayList<>();

    @Override
    public void subscribe(WebSocketData webSocketData) {
        long s = System.currentTimeMillis();
        hashes.add(webSocketData.getRequestHash());
        if (transactions == null) {
            transactions = pendingTxQueryService.queryPendingTxList();
            this.responseChannel = webSocketData.getResponseChannel();
            List<String> newHashes = new ArrayList<>();
            for (Transaction transaction : transactions) {
                String hash = transaction.getHash();
                newHashes.add(hash);
                if (preTransactionHashes.contains(hash)) {
                    continue;
                }
                responses.add(WebSocketResponse.build(hash, hashes, s));
            }
            preTransactionHashes = newHashes;
        }
    }

}

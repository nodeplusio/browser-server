package com.platon.browser.service;

import com.alibaba.fastjson.JSONObject;
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
public class PendingTransactionsService implements SubscriptionService {

    @Resource
    private PendingTxQueryService pendingTxQueryService;

    @Override
    public void subscribe(Map.Entry<Session, WebSocketData> entry, Map.Entry<String, Request> request) {
        List<Object> requestParam = request.getValue().getParams();
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
        WebSocketData webSocketData = entry.getValue();
        for (Transaction transaction : transactions) {
            String hash = transaction.getHash();
            hashes.add(hash);
            if (webSocketData.getHashes().contains(hash)
                    || !fromAddressList.isEmpty() && !fromAddressList.contains(transaction.getFrom())
                    || !toAddressList.isEmpty() && !toAddressList.contains(transaction.getTo())) {
                continue;
            }

            if (hashesOnly) {
                send(entry, request, hash);
            } else {
                send(entry, request, new TransactionResult(transaction));
            }
        }
        webSocketData.setHashes(hashes);
    }

}

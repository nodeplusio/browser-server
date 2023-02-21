package com.platon.browser.service;

import com.alibaba.fastjson.JSONObject;
import com.platon.bech32.Bech32;
import com.platon.browser.service.tx.PendingTxQueryService;
import com.platon.browser.util.JsonUtil;
import com.platon.browser.websocket.WebSocketData;
import com.platon.browser.websocket.WebSocketResponse;
import com.platon.parameters.NetworkParameters;
import com.platon.protocol.core.methods.response.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Scope("prototype")
@Slf4j
public class PendingTransactionsService extends AbstractSubscriptionService {

    @Resource
    private PendingTxQueryService pendingTxQueryService;

    private List<Transaction> transactions;
    private static Map<String, List<String>> preTransactionHashMap = new HashMap<>();
    private Map<String, List<String>> hashMap = new HashMap<>();

    void init(WebSocketData webSocketData) {
        this.responseChannel = webSocketData.getResponseChannel();
        transactions = pendingTxQueryService.queryPendingTxList();
        NetworkParameters.selectPlatON();
        for (Transaction transaction : transactions) {
            String from = Bech32.addressDecodeHex(transaction.getFrom());
            String to = Bech32.addressDecodeHex(transaction.getTo());
            transaction.setFrom(from);
            transaction.setTo(to);
        }
    }

    @Override
    public void subscribe(WebSocketData webSocketData) {
        long s = System.currentTimeMillis();
        if (transactions == null) {
            init(webSocketData);
        }
        List<Object> requestParam = webSocketData.getRequest().getParams();
        String key = JsonUtil.toJson(requestParam);
        List<String> list;
        if (hashMap.containsKey(key)) {
            list = hashMap.get(key);
        } else {
            list = new ArrayList<>();
            hashMap.put(key, list);
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
            List<String> newHashes = new ArrayList<>();
            for (Transaction transaction : transactions) {
                String hash = transaction.getHash();
                newHashes.add(hash);
                List<String> preTransactions = preTransactionHashMap.get(key);
                if (preTransactions != null && preTransactions.contains(hash)
                        || !fromAddressList.isEmpty() && !fromAddressList.contains(transaction.getFrom())
                        || !toAddressList.isEmpty() && !toAddressList.contains(transaction.getTo())) {
                    continue;
                }

                if (hashesOnly) {
                    responses.add(WebSocketResponse.build(hash, list, s));
                } else {
                    responses.add(WebSocketResponse.build(new TransactionResult(transaction), list, s));
                }
            }
            preTransactionHashMap.put(key, newHashes);
        }
        list.add(webSocketData.getRequestHash());
    }

}

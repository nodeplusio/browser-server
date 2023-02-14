package com.platon.browser.service;

import com.alibaba.fastjson.JSONObject;
import com.platon.bech32.Bech32;
import com.platon.browser.service.tx.PendingTxQueryService;
import com.platon.browser.util.JsonUtil;
import com.platon.browser.websocket.WebSocketData;
import com.platon.parameters.NetworkParameters;
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
public class PendingTransactionsService implements SubscriptionService {

    @Resource
    private PendingTxQueryService pendingTxQueryService;

    @Resource
    private WebSocketService webSocketService;
    private List<Transaction> transactions;

    @PostConstruct
    void init() {
        transactions = pendingTxQueryService.queryPendingTxList();
        for (Transaction transaction : transactions) {
            String from = Bech32.addressDecodeHex(transaction.getFrom());
            String to = Bech32.addressDecodeHex(transaction.getTo());
            transaction.setFrom(from);
            transaction.setTo(to);
        }
    }

    @Override
    public void subscribe(WebSocketData webSocketData) {
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
        List<String> hashes = new ArrayList<>();
        NetworkParameters.selectPlatON();
        String lastPushData = webSocketData.getLastPushData();
        for (Transaction transaction : transactions) {
            String hash = transaction.getHash();
            hashes.add(hash);
            if (lastPushData != null && lastPushData.contains(hash)
                    || !fromAddressList.isEmpty() && !fromAddressList.contains(transaction.getFrom())
                    || !toAddressList.isEmpty() && !toAddressList.contains(transaction.getTo())) {
                continue;
            }

            if (hashesOnly) {
                webSocketService.send(webSocketData, hash);
            } else {
                webSocketService.send(webSocketData, new TransactionResult(transaction));
            }
        }
        webSocketData.setLastPushData(JsonUtil.toJson(hashes));
    }

}

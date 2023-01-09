package com.platon.browser.service;

import com.alibaba.fastjson.JSONObject;
import com.platon.browser.client.PlatOnClient;
import com.platon.browser.elasticsearch.dto.Transaction;
import com.platon.browser.service.elasticsearch.EsTransactionRepository;
import com.platon.browser.service.elasticsearch.bean.ESResult;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilderConstructor;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilders;
import com.platon.browser.websocket.Request;
import com.platon.browser.websocket.WebSocketData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MinedTransactionsService implements SubscriptionService {

    @Resource
    private EsTransactionRepository esTransactionRepository;
    @Resource
    private PlatOnClient platOnClient;

    @Override
    public void subscribe(Map.Entry<Session, WebSocketData> entry, Map.Entry<String, Request> request) {
        List<Object> requestParam = request.getValue().getParams();
        List<MinedTransactionsAddress> addresses = new ArrayList<>();
        Boolean hashesOnly = false;
        if (requestParam.size() > 1) {
            JSONObject param = (JSONObject) requestParam.get(1);
            MinedTransactionsParam minedTransactionsParam = param.toJavaObject(MinedTransactionsParam.class);
            hashesOnly = minedTransactionsParam.getHashesOnly();
            addresses = minedTransactionsParam.getAddresses();
        }
        WebSocketData value = entry.getValue();
        ESQueryBuilderConstructor blockConstructor = new ESQueryBuilderConstructor();
        int pageSize;
        if (value.getBlockNum() == null) {
            pageSize = 1;
            blockConstructor.setDesc("num");
        } else {
            blockConstructor.must(new ESQueryBuilders().range("num", value.getBlockNum() + 1, null)).setAsc("num");
            pageSize = 10;
        }
        try {
            ESResult<Transaction> transactionList = esTransactionRepository.search(blockConstructor, Transaction.class, 1, pageSize);
            for (Transaction transaction : transactionList.getRsData()) {
                if (!addresses.isEmpty() && addresses.stream().noneMatch(address ->
                        (address.getFrom() == null || address.getFrom().equals(transaction.getFrom()))
                        && (address.getTo() == null || address.getTo().equals(transaction.getTo())))) {
                    continue;
                }
                if (hashesOnly) {
                    MinedTransactionResult<MinedTransactionOnlyHash> minedTransactionResult = new MinedTransactionResult<>();
                    MinedTransactionOnlyHash minedTransactionOnlyHash = new MinedTransactionOnlyHash();
                    minedTransactionOnlyHash.setHash(transaction.getHash());
                    minedTransactionResult.setTransaction(minedTransactionOnlyHash);
                    send(entry, request, transaction.getHash());
                } else {
                    com.platon.protocol.core.methods.response.Transaction result = platOnClient.getWeb3jWrapper().getWeb3j()
                            .platonGetTransactionByHash(transaction.getHash())
                            .send().getResult();
                    MinedTransactionResult<TransactionResult> minedTransactionResult = new MinedTransactionResult<>();
                    minedTransactionResult.setTransaction(new TransactionResult(result));
                    send(entry, request, minedTransactionResult);
                }
                value.setBlockNum(transaction.getNum());
            }
        } catch (IOException e) {
            log.error("查询es异常", e);
        }
    }

}

package com.platon.browser.service;

import com.alibaba.fastjson.JSONObject;
import com.platon.browser.elasticsearch.dto.TransactionOrigin;
import com.platon.browser.service.elasticsearch.EsTransactionOriginRepository;
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
    private EsTransactionOriginRepository esTransactionOriginRepository;

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
        String blockNumberFieldName = "blockNumber";
        if (value.getBlockNum() == null) {
            pageSize = 1;
            blockConstructor.setDesc(blockNumberFieldName);
        } else {
            blockConstructor.must(new ESQueryBuilders().range(blockNumberFieldName, value.getBlockNum() + 1, null)).setAsc(blockNumberFieldName);
            pageSize = 10;
        }
        try {
            ESResult<TransactionOrigin> transactionList = esTransactionOriginRepository.search(blockConstructor, TransactionOrigin.class, 1, pageSize);
            for (TransactionOrigin transaction : transactionList.getRsData()) {
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
                    MinedTransactionResult<TransactionResult> minedTransactionResult = new MinedTransactionResult<>();
                    minedTransactionResult.setTransaction(new TransactionResult(transaction));
                    send(entry, request, minedTransactionResult);
                }
                value.setBlockNum(transaction.getBlockNumber().longValue());
            }
        } catch (IOException e) {
            log.error("查询es异常", e);
        }
    }

}

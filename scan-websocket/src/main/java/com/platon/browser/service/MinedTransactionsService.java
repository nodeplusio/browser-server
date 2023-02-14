package com.platon.browser.service;

import com.alibaba.fastjson.JSONObject;
import com.platon.browser.elasticsearch.dto.TransactionOrigin;
import com.platon.browser.service.elasticsearch.EsTransactionOriginRepository;
import com.platon.browser.service.elasticsearch.bean.ESResult;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilderConstructor;
import com.platon.browser.websocket.WebSocketData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.platon.browser.service.ESQueryBuilderConstructorBuilder.buildBlockConstructor;

@Service
@Scope("prototype")
@Slf4j
public class MinedTransactionsService implements SubscriptionService {

    @Resource
    private EsTransactionOriginRepository esTransactionOriginRepository;
    @Resource
    private WebSocketService webSocketService;
    List<TransactionOrigin> rsData = null;

    @Override
    public void subscribe(WebSocketData webSocketData) {
        if (rsData == null) {
            rsData = queryData(webSocketData);
        }
        List<Object> requestParam = webSocketData.getRequest().getParams();
        List<MinedTransactionsAddress> addresses = new ArrayList<>();
        Boolean hashesOnly = false;
        if (requestParam.size() > 1) {
            JSONObject param = (JSONObject) requestParam.get(1);
            MinedTransactionsParam minedTransactionsParam = param.toJavaObject(MinedTransactionsParam.class);
            hashesOnly = minedTransactionsParam.getHashesOnly();
            addresses = minedTransactionsParam.getAddresses();
        }
        Long blockNumber = ESQueryBuilderConstructorBuilder.getBlockNumber(webSocketData);
        for (TransactionOrigin transaction : rsData) {
            long number = transaction.getBlockNumber().longValue();
            if (blockNumber != null && number <= blockNumber) {
                continue;
            }
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
                webSocketService.send(webSocketData, transaction.getHash());
            } else {
                MinedTransactionResult<TransactionResult> minedTransactionResult = new MinedTransactionResult<>();
                minedTransactionResult.setTransaction(new TransactionResult(transaction));
                webSocketService.send(webSocketData, minedTransactionResult);
            }
            webSocketData.setLastPushData("" + number);
        }
    }

    public List<TransactionOrigin> queryData(WebSocketData webSocketData) {
        ESQueryBuilderConstructor blockConstructor = buildBlockConstructor(webSocketData, "blockNumber");
        try {
            ESResult<TransactionOrigin> transactionList = esTransactionOriginRepository.search(blockConstructor, TransactionOrigin.class, 1, blockConstructor.getSize());
            return transactionList.getRsData();
        } catch (IOException e) {
            log.error("查询es异常", e);
        }
        return new ArrayList<>();
    }

}

package com.platon.browser.service;

import com.alibaba.fastjson.JSONObject;
import com.platon.browser.elasticsearch.dto.TransactionOrigin;
import com.platon.browser.service.elasticsearch.EsTransactionOriginRepository;
import com.platon.browser.service.elasticsearch.bean.ESResult;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilderConstructor;
import com.platon.browser.util.JsonUtil;
import com.platon.browser.websocket.WebSocketData;
import com.platon.browser.websocket.WebSocketResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.platon.browser.service.ESQueryBuilderConstructorBuilder.buildBlockConstructor;

@Service
@Scope("prototype")
@Slf4j
public class MinedTransactionsService extends AbstractSubscriptionService {

    @Resource
    private EsTransactionOriginRepository esTransactionOriginRepository;
    List<TransactionOrigin> rsData = null;
    private static Long blockNumber;
    private Map<String, List<String>> hashMap = new HashMap<>();

    @Override
    public void subscribe(WebSocketData webSocketData) {
        long s = System.currentTimeMillis();
        if (rsData == null) {
            responseChannel = webSocketData.getResponseChannel();
            rsData = queryData();
        }
        List<Object> requestParam = webSocketData.getRequest().getParams();
        String key = JsonUtil.toJson(requestParam);
        List<String> list;
        if (hashMap.containsKey(key)) {
            list = hashMap.get(key);
        } else {
            list = new ArrayList<>();
            List<MinedTransactionsAddress> addresses = new ArrayList<>();
            Boolean hashesOnly = false;
            if (requestParam.size() > 1) {
                JSONObject param = (JSONObject) requestParam.get(1);
                MinedTransactionsParam minedTransactionsParam = param.toJavaObject(MinedTransactionsParam.class);
                hashesOnly = minedTransactionsParam.getHashesOnly();
                addresses = minedTransactionsParam.getAddresses();
            }
            for (TransactionOrigin transaction : rsData) {
                blockNumber = transaction.getBlockNumber().longValue();

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
                    responses.add(WebSocketResponse.build(minedTransactionResult, list, s));
                } else {
                    MinedTransactionResult<TransactionResult> minedTransactionResult = new MinedTransactionResult<>();
                    minedTransactionResult.setTransaction(new TransactionResult(transaction));
                    responses.add(WebSocketResponse.build(minedTransactionResult, list, s));
                }
            }
        }
        list.add(webSocketData.getRequestHash());
    }

    public List<TransactionOrigin> queryData() {
        ESQueryBuilderConstructor blockConstructor = buildBlockConstructor("blockNumber", blockNumber);
        try {
            ESResult<TransactionOrigin> transactionList = esTransactionOriginRepository.search(blockConstructor, TransactionOrigin.class, 1, blockConstructor.getSize());
            return transactionList.getRsData();
        } catch (IOException e) {
            log.error("查询es异常", e);
        }
        return new ArrayList<>();
    }

    @Override
    public void clean() {
        blockNumber = null;
    }

}

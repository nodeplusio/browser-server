package com.platon.browser.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.platon.browser.config.RedisKeyConfig;
import com.platon.browser.elasticsearch.dto.TransactionOrigin;
import com.platon.browser.service.elasticsearch.EsTransactionOriginRepository;
import com.platon.browser.service.elasticsearch.bean.ESResult;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilderConstructor;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilders;
import com.platon.browser.websocket.WebSocketChannelData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MinedTransactionsService implements SubscriptionService {

    @Resource
    private EsTransactionOriginRepository esTransactionOriginRepository;
    @Resource
    private RedisKeyConfig redisKeyConfig;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private WebSocketService webSocketService;

    @Override
    public void subscribe(WebSocketChannelData webSocketData) {
        List<Object> requestParam = webSocketData.getRequest().getParams();
        List<MinedTransactionsAddress> addresses = new ArrayList<>();
        Boolean hashesOnly = false;
        if (requestParam.size() > 1) {
            JSONObject param = (JSONObject) requestParam.get(1);
            MinedTransactionsParam minedTransactionsParam = param.toJavaObject(MinedTransactionsParam.class);
            hashesOnly = minedTransactionsParam.getHashesOnly();
            addresses = minedTransactionsParam.getAddresses();
        }
        ESQueryBuilderConstructor blockConstructor = new ESQueryBuilderConstructor();
        int pageSize;
        String blockNumberFieldName = "blockNumber";
        if (webSocketData.getBlockNum() == null) {
            pageSize = 1;
            blockConstructor.setDesc(blockNumberFieldName);
        } else {
            blockConstructor.must(new ESQueryBuilders().range(blockNumberFieldName, webSocketData.getBlockNum() + 1, null)).setAsc(blockNumberFieldName);
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
                    webSocketService.send(webSocketData, transaction.getHash());
                } else {
                    MinedTransactionResult<TransactionResult> minedTransactionResult = new MinedTransactionResult<>();
                    minedTransactionResult.setTransaction(new TransactionResult(transaction));
                    webSocketService.send(webSocketData, minedTransactionResult);
                }
                webSocketData.setBlockNum(transaction.getBlockNumber().longValue());
                HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
                hashOperations.put(redisKeyConfig.getPushData(), webSocketData.getRequestHash(), JSON.toJSONString(webSocketData));
            }
        } catch (IOException e) {
            log.error("查询es异常", e);
        }
    }

}

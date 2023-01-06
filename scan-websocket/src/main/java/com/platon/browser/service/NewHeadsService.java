package com.platon.browser.service;

import com.alibaba.fastjson.JSON;
import com.platon.browser.elasticsearch.dto.Block;
import com.platon.browser.service.elasticsearch.EsBlockRepository;
import com.platon.browser.service.elasticsearch.bean.ESResult;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilderConstructor;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilders;
import com.platon.browser.websocket.Params;
import com.platon.browser.websocket.Request;
import com.platon.browser.websocket.SubscriptionResponse;
import com.platon.browser.websocket.WebSocketData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class NewHeadsService implements SubscriptionService {

    @Resource
    private EsBlockRepository esBlockRepository;

    @Override
    public void push(Map.Entry<Session, WebSocketData> entry, Map.Entry<String, Request> request) {
        Session key = entry.getKey();
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
            ESResult<Block> blockList = esBlockRepository.search(blockConstructor, Block.class, 1, pageSize);
            for (Block rsDatum : blockList.getRsData()) {
                send(key, request, rsDatum);
                value.setBlockNum(rsDatum.getNum());
            }
        } catch (IOException e) {
            log.error("查询es异常", e);
        }
    }

    private void send(Session key, Map.Entry<String, Request> stringRequestEntry, Block result) {
        SubscriptionResponse<Block> response = new SubscriptionResponse<>();
        response.setJsonrpc(stringRequestEntry.getValue().getJsonrpc());
        response.setMethod("eth_subscription");
        Params<Block> params = new Params<>();
        params.setSubscription(stringRequestEntry.getKey());
        params.setResult(result);
        response.setParams(params);
        RemoteEndpoint.Async asyncRemote = key.getAsyncRemote();
        asyncRemote.sendText(JSON.toJSONString(response));
    }
}

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class NewHeadsTask {

    @Autowired
    private WebSocketService webSocketService;
    @Resource
    private EsBlockRepository esBlockRepository;

    /**
     * 推送NewHeads
     */
    @Scheduled(cron = "0/1 * * * * ?")
    public void pushNewHeads() {
        Map<Session, WebSocketData> map = WebSocketService.getMap();
        map.forEach((key, value) -> value.getRequests().entrySet().stream()
                .filter(request -> "newHeads".equals(request.getValue().getParams().get(0)))
                .forEach(stringRequestEntry -> {
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
                            send(key, stringRequestEntry, rsDatum);
                            value.setBlockNum(rsDatum.getNum());
                        }
                    } catch (IOException e) {
                        log.error("查询es异常", e);
                    }
        }));
    }

    private static void send(Session key, Map.Entry<String, Request> stringRequestEntry, Block result) {
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

package com.platon.browser.service;

import com.platon.browser.elasticsearch.dto.BlockOrigin;
import com.platon.browser.service.elasticsearch.EsBlockOriginRepository;
import com.platon.browser.service.elasticsearch.bean.ESResult;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilderConstructor;
import com.platon.browser.util.JsonUtil;
import com.platon.browser.websocket.Params;
import com.platon.browser.websocket.SubscriptionResponse;
import com.platon.browser.websocket.WebSocketData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.platon.browser.service.ESQueryBuilderConstructorBuilder.buildBlockConstructor;

@Service
@Scope("prototype")
@Slf4j
public class NewHeadsService implements SubscriptionService {

    @Resource
    private EsBlockOriginRepository esBlockOriginRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private List<BlockOrigin> rsData = null;
    @Getter
    private List<BlockResult> result = new ArrayList<>();
    private List<String> hashes = new ArrayList<>();
    WebSocketData webSocketData = new WebSocketData();

    @Override
    public void subscribe(WebSocketData webSocketData) {
        long s = System.currentTimeMillis();
        hashes.add(webSocketData.getRequestHash());
        if (rsData == null) {
            this.webSocketData = webSocketData;
            rsData = queryData(webSocketData);
            log.debug("queryData 耗时:{} ms", System.currentTimeMillis() - s);
            Long blockNumber = ESQueryBuilderConstructorBuilder.getBlockNumber(webSocketData);
            for (BlockOrigin blockOrigin : rsData) {
                long number = blockOrigin.getNumber().longValue();
                if (blockNumber != null && number <= blockNumber) {
                    continue;
                }
                result.add(new BlockResult(blockOrigin));
                webSocketData.setLastPushData("" + number);
            }
        }
    }

    public List<BlockOrigin> queryData(WebSocketData webSocketData) {
        ESQueryBuilderConstructor blockConstructor = buildBlockConstructor(webSocketData, "number");
        try {
            ESResult<BlockOrigin> blockList = esBlockOriginRepository.search(blockConstructor, BlockOrigin.class, 1, blockConstructor.getSize());
            return blockList.getRsData();
        } catch (IOException e) {
            log.error("查询es异常", e);
        }
        return new ArrayList<>();
    }

    @Override
    public void send() {

        List<WebSocketData> list = new ArrayList<>();
        for (BlockResult blockResult : result) {
            WebSocketData webSocketData = new WebSocketData();
            SubscriptionResponse<Object> response = new SubscriptionResponse<>();
            Params<Object> params = new Params<>();
            params.setSubscription(webSocketData.getRequestHash());
            params.setResult(blockResult);
            response.setParams(params);

            webSocketData.setResponse(response);
            webSocketData.setHashes(hashes);
            list.add(webSocketData);
        }
        String message = JsonUtil.toJson(list);
        redisTemplate.convertAndSend(webSocketData.getResponseChannel(), message);
    }
}

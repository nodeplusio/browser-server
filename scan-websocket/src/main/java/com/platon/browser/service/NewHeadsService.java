package com.platon.browser.service;

import com.alibaba.fastjson.JSON;
import com.platon.browser.config.RedisKeyConfig;
import com.platon.browser.elasticsearch.dto.BlockOrigin;
import com.platon.browser.service.elasticsearch.EsBlockOriginRepository;
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

@Service
@Slf4j
public class NewHeadsService implements SubscriptionService {

    @Resource
    private EsBlockOriginRepository esBlockOriginRepository;
    @Resource
    private RedisKeyConfig redisKeyConfig;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private WebSocketService webSocketService;

    @Override
    public void subscribe(WebSocketChannelData webSocketData) {
        ESQueryBuilderConstructor blockConstructor = new ESQueryBuilderConstructor();
        int pageSize;
        String numberFieldName = "number";
        if (webSocketData.getBlockNum() == null) {
            pageSize = 1;
            blockConstructor.setDesc(numberFieldName);
        } else {
            blockConstructor.must(new ESQueryBuilders().range(numberFieldName, webSocketData.getBlockNum() + 1, null)).setAsc(numberFieldName);
            pageSize = 10;
        }
        try {
            ESResult<BlockOrigin> blockList = esBlockOriginRepository.search(blockConstructor, BlockOrigin.class, 1, pageSize);
            for (BlockOrigin block : blockList.getRsData()) {
                webSocketService.send(webSocketData, new BlockResult(block));
                webSocketData.setBlockNum(block.getNumber().longValue());
                HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
                hashOperations.put(redisKeyConfig.getPushData(), webSocketData.getRequestHash(), JSON.toJSONString(webSocketData));
            }
        } catch (IOException e) {
            log.error("查询es异常", e);
        }
    }

}

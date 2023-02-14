package com.platon.browser.service;

import com.platon.browser.elasticsearch.dto.BlockOrigin;
import com.platon.browser.service.elasticsearch.EsBlockOriginRepository;
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
public class NewHeadsService implements SubscriptionService {

    @Resource
    private EsBlockOriginRepository esBlockOriginRepository;
    @Resource
    private WebSocketService webSocketService;

    private List<BlockOrigin> rsData = null;

    @Override
    public void subscribe(WebSocketData webSocketData) {
        long s = System.currentTimeMillis();
        if (rsData == null) {
            rsData = queryData(webSocketData);
            log.debug("queryData 耗时:{} ms", System.currentTimeMillis() - s);
        }
        Long blockNumber = ESQueryBuilderConstructorBuilder.getBlockNumber(webSocketData);
        for (BlockOrigin blockOrigin : rsData) {
            long number = blockOrigin.getNumber().longValue();
            if (blockNumber != null && number <= blockNumber) {
                continue;
            }
            s = System.currentTimeMillis();
            webSocketService.send(webSocketData, new BlockResult(blockOrigin));
            log.debug("webSocketService.send 耗时:{} ms", System.currentTimeMillis() - s);
            webSocketData.setLastPushData("" + number);
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

}

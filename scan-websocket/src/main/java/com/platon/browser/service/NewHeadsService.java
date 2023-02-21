package com.platon.browser.service;

import com.platon.browser.elasticsearch.dto.BlockOrigin;
import com.platon.browser.service.elasticsearch.EsBlockOriginRepository;
import com.platon.browser.service.elasticsearch.bean.ESResult;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilderConstructor;
import com.platon.browser.websocket.WebSocketData;
import com.platon.browser.websocket.WebSocketResponse;
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
public class NewHeadsService extends AbstractSubscriptionService {

    @Resource
    private EsBlockOriginRepository esBlockOriginRepository;

    private List<String> hashes = new ArrayList<>();
    private static Long blockNumber;

    @Override
    public void subscribe(WebSocketData webSocketData) {
        long s = System.currentTimeMillis();
        hashes.add(webSocketData.getRequestHash());
        if (responses.isEmpty()) {
            this.responseChannel = webSocketData.getResponseChannel();
            List<BlockOrigin> rsData = queryData();
            log.debug("queryData 耗时:{} ms", System.currentTimeMillis() - s);
            for (BlockOrigin blockOrigin : rsData) {
                blockNumber = blockOrigin.getNumber().longValue();

                responses.add(WebSocketResponse.build(new BlockResult(blockOrigin), hashes, s));
            }
        }
    }

    public List<BlockOrigin> queryData() {
        ESQueryBuilderConstructor blockConstructor = buildBlockConstructor("number", blockNumber);
        try {
            ESResult<BlockOrigin> blockList = esBlockOriginRepository.search(blockConstructor, BlockOrigin.class, 1, blockConstructor.getSize());
            return blockList.getRsData();
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

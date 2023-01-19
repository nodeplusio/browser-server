package com.platon.browser.service;

import com.platon.browser.elasticsearch.dto.BlockOrigin;
import com.platon.browser.service.elasticsearch.EsBlockOriginRepository;
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
import java.util.Map;

@Service
@Slf4j
public class NewHeadsService implements SubscriptionService {

    @Resource
    private EsBlockOriginRepository esBlockOriginRepository;

    @Override
    public void subscribe(Map.Entry<Session, WebSocketData> entry, Map.Entry<String, Request> request) {
        WebSocketData value = entry.getValue();
        ESQueryBuilderConstructor blockConstructor = new ESQueryBuilderConstructor();
        int pageSize;
        String numberFieldName = "number";
        if (value.getBlockNum() == null) {
            pageSize = 1;
            blockConstructor.setDesc(numberFieldName);
        } else {
            blockConstructor.must(new ESQueryBuilders().range(numberFieldName, value.getBlockNum() + 1, null)).setAsc(numberFieldName);
            pageSize = 10;
        }
        try {
            ESResult<BlockOrigin> blockList = esBlockOriginRepository.search(blockConstructor, BlockOrigin.class, 1, pageSize);
            for (BlockOrigin block : blockList.getRsData()) {
                send(entry, request, new BlockResult(block));
                value.setBlockNum(block.getNumber().longValue());
            }
        } catch (IOException e) {
            log.error("查询es异常", e);
        }
    }

}

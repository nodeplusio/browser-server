package com.platon.browser.service;

import com.platon.browser.client.PlatOnClient;
import com.platon.browser.elasticsearch.dto.Block;
import com.platon.browser.service.elasticsearch.EsBlockRepository;
import com.platon.browser.service.elasticsearch.bean.ESResult;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilderConstructor;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilders;
import com.platon.browser.websocket.Request;
import com.platon.browser.websocket.WebSocketData;
import com.platon.protocol.core.DefaultBlockParameter;
import com.platon.protocol.core.methods.response.PlatonBlock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

@Service
@Slf4j
public class NewHeadsService implements SubscriptionService {

    @Resource
    private EsBlockRepository esBlockRepository;
    @Resource
    private PlatOnClient platOnClient;

    @Override
    public void subscribe(Map.Entry<Session, WebSocketData> entry, Map.Entry<String, Request> request) {
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
            for (Block block : blockList.getRsData()) {
                PlatonBlock.Block result = platOnClient.getWeb3jWrapper().getWeb3j()
                        .platonGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(block.getNum())), false)
                        .send().getResult();
                send(entry, request, new BlockResult(result));
                value.setBlockNum(block.getNum());
            }
        } catch (IOException e) {
            log.error("查询es异常", e);
        }
    }

}
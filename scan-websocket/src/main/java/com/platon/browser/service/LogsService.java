package com.platon.browser.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.platon.browser.client.PlatOnClient;
import com.platon.browser.elasticsearch.dto.Block;
import com.platon.browser.service.elasticsearch.EsBlockRepository;
import com.platon.browser.service.elasticsearch.bean.ESResult;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilderConstructor;
import com.platon.browser.websocket.Request;
import com.platon.browser.websocket.WebSocketData;
import com.platon.protocol.core.DefaultBlockParameter;
import com.platon.protocol.core.methods.request.PlatonFilter;
import com.platon.protocol.core.methods.response.Log;
import com.platon.protocol.core.methods.response.PlatonLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class LogsService implements SubscriptionService {

    @Resource
    private EsBlockRepository esBlockRepository;
    @Resource
    private PlatOnClient platOnClient;

    @Override
    public void subscribe(Map.Entry<Session, WebSocketData> entry, Map.Entry<String, Request> request) {
        List<Object> requestParam = request.getValue().getParams();
        List<String> addressList = new ArrayList<>();
        List<List<String>> topicList = new ArrayList<>();
        if (requestParam.size() > 1) {
            JSONObject param = (JSONObject) requestParam.get(1);
            Object addresses = param.get("address");
            if (addresses instanceof String) {
                addressList.add((String) addresses);
            } else if (addresses instanceof List) {
                addressList.addAll((List<String>) addresses);
            }
            JSONArray topics = param.getJSONArray("topics");
            if (topics != null) {
                for (Object topic : topics) {
                    if (topic == null || topic instanceof String) {
                        topicList.add(Arrays.asList((String) topic));
                    } else if (addresses instanceof List) {
                        topicList.add((List<String>) topic);
                    }
                }
            }
        }
        WebSocketData value = entry.getValue();
        ESQueryBuilderConstructor blockConstructor = new ESQueryBuilderConstructor();
        blockConstructor.setDesc("num");
        try {
            ESResult<Block> blockList = esBlockRepository.search(blockConstructor, Block.class, 1, 1);
            if (blockList.getRsData().isEmpty()) {
                return;
            }
            Long num = blockList.getRsData().get(0).getNum();
            DefaultBlockParameter toBlock = DefaultBlockParameter.valueOf(BigInteger.valueOf(num));
            DefaultBlockParameter fromBlock;
            if (value.getBlockNum() == null) {
                fromBlock = toBlock;
            } else {
                if (value.getBlockNum() >= num) {
                    return;
                }
                fromBlock = DefaultBlockParameter.valueOf(BigInteger.valueOf(value.getBlockNum() + 1));
            }
            PlatonFilter platonFilter = new PlatonFilter(fromBlock, toBlock, addressList);
            List<PlatonLog.LogResult> result = platOnClient.getWeb3jWrapper().getWeb3j()
                    .platonGetLogs(platonFilter)
                    .send().getResult();
            for (PlatonLog.LogResult logResult : result) {
                Log log = (Log) logResult.get();
                List<String> topics = log.getTopics();
                if (matchTopic(topicList, topics)) {
                    send(entry, request, new LogResult(log));
                }
            }
            value.setBlockNum(num);
        } catch (IOException e) {
            log.error("查询es异常", e);
        }
    }

    private boolean matchTopic(List<List<String>> topicParamList, List<String> topics) {
        if (topicParamList.isEmpty()) {
            return true;
        }
        if (topicParamList.size() == 1) {
            List<String> strings = topicParamList.get(0);
            if (strings.isEmpty()) {
                return true;
            }
            return topics.contains(strings.get(0));
        }
        if (topicParamList.size() == 2) {
            if (topics.size() < 2) {
                return false;
            }
            List<String> first = topicParamList.get(0);
            List<String> second = topicParamList.get(1);
            if (first == null || first.isEmpty()) {
                return false;
            }
            if (!(first.size() == 1 && first.get(0) == null || first.contains(topics.get(0)))) {
                return false;
            }
            return second.contains(topics.get(1));
        }
        return false;
    }

}

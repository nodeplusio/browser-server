package com.platon.browser.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.platon.browser.elasticsearch.dto.LogOrigin;
import com.platon.browser.service.elasticsearch.EsLogOriginRepository;
import com.platon.browser.service.elasticsearch.bean.ESResult;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilderConstructor;
import com.platon.browser.websocket.WebSocketData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.platon.browser.service.ESQueryBuilderConstructorBuilder.buildBlockConstructor;

@Service
@Scope("prototype")
@Slf4j
public class LogsService implements SubscriptionService {

    @Resource
    private EsLogOriginRepository esLogOriginRepository;
    @Resource
    private WebSocketService webSocketService;
    private List<LogOrigin> rsData = null;

    @Override
    public void subscribe(WebSocketData webSocketData) {
        List<Object> requestParam = webSocketData.getRequest().getParams();
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
        if (rsData == null) {
            rsData = queryData(webSocketData);
        }
        Long blockNumber = ESQueryBuilderConstructorBuilder.getBlockNumber(webSocketData);
        for (LogOrigin logOrigin : rsData) {
            long number = logOrigin.getBlockNumber().longValue();
            if (blockNumber != null && number <= blockNumber) {
                continue;
            }
            webSocketData.setLastPushData("" + number);
            if (matchTopic(topicList, logOrigin.getTopics())) {
                webSocketService.send(webSocketData, new LogResult(logOrigin));
            }
        }
    }

    public List<LogOrigin> queryData(WebSocketData webSocketData) {
        ESQueryBuilderConstructor blockConstructor = buildBlockConstructor(webSocketData, "blockNumber");
        try {
            ESResult<LogOrigin> blockList = esLogOriginRepository.search(blockConstructor, LogOrigin.class, 1, blockConstructor.getSize());
            return blockList.getRsData();
        } catch (IOException e) {
            log.error("查询es异常", e);
        }
        return new ArrayList<>();
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

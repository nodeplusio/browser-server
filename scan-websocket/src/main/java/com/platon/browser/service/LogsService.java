package com.platon.browser.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.platon.browser.elasticsearch.dto.LogOrigin;
import com.platon.browser.service.elasticsearch.EsLogOriginRepository;
import com.platon.browser.service.elasticsearch.bean.ESResult;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilderConstructor;
import com.platon.browser.util.JsonUtil;
import com.platon.browser.websocket.WebSocketData;
import com.platon.browser.websocket.WebSocketResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

import static com.platon.browser.service.ESQueryBuilderConstructorBuilder.buildBlockConstructor;

@Service
@Scope("prototype")
@Slf4j
public class LogsService extends AbstractSubscriptionService {

    @Resource
    private EsLogOriginRepository esLogOriginRepository;
    private Map<String, List<String>> hashMap= new HashMap<>();

    private static Long blockNumber;
    private List<LogOrigin> rsData;

    @Override
    public void subscribe(WebSocketData webSocketData) {
        long s = System.currentTimeMillis();
        if (rsData == null) {
            responseChannel = webSocketData.getResponseChannel();
            rsData = queryData();
        }

        List<Object> requestParam = webSocketData.getRequest().getParams();
        String key = JsonUtil.toJson(requestParam);
        List<String> list;
        if (hashMap.containsKey(key)) {
            list = hashMap.get(key);
        } else {
            list = new ArrayList<>();
            hashMap.put(key, list);
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
            for (LogOrigin logOrigin : rsData) {
                blockNumber = logOrigin.getBlockNumber().longValue();
                if (matchAddress(logOrigin, addressList) && matchTopic(topicList, logOrigin.getTopics())) {
                    responses.add(WebSocketResponse.build(new LogResult(logOrigin), list, s));
                }
            }
        }
        list.add(webSocketData.getRequestHash());

    }

    private boolean matchAddress(LogOrigin logOrigin, List<String> addressList) {
        if (addressList.isEmpty()) {
            return true;
        }
        return addressList.contains(logOrigin.getAddress());
    }

    public List<LogOrigin> queryData() {
        ESQueryBuilderConstructor blockConstructor = buildBlockConstructor("blockNumber", blockNumber);
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

    @Override
    public void clean() {
        blockNumber = null;
    }

}

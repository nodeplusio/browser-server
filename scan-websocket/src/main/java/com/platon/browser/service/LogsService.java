package com.platon.browser.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.platon.browser.config.RedisKeyConfig;
import com.platon.browser.elasticsearch.dto.LogOrigin;
import com.platon.browser.service.elasticsearch.EsLogOriginRepository;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class LogsService implements SubscriptionService {

    @Resource
    private EsLogOriginRepository esLogOriginRepository;
    @Resource
    private RedisKeyConfig redisKeyConfig;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private WebSocketService webSocketService;

    @Override
    public void subscribe(WebSocketChannelData webSocketData) {
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
        ESQueryBuilderConstructor blockConstructor = new ESQueryBuilderConstructor();
        int pageSize;
        String blockNumberFieldName = "blockNumber";
        if (webSocketData.getBlockNum() == null) {
            pageSize = 1;
            blockConstructor.setDesc(blockNumberFieldName);
        } else {
            blockConstructor.must(new ESQueryBuilders().range(blockNumberFieldName, webSocketData.getBlockNum() + 1, null)).setAsc(blockNumberFieldName);
            pageSize = 10;
        }
        try {
            ESResult<LogOrigin> blockList = esLogOriginRepository.search(blockConstructor, LogOrigin.class, 1, pageSize);
            if (blockList.getRsData().isEmpty()) {
                return;
            }
            Long num = blockList.getRsData().get(0).getBlockNumber().longValue();
            for (LogOrigin logOrigin : blockList.getRsData()) {
                List<String> topics = logOrigin.getTopics();
                if (matchTopic(topicList, topics)) {
                    webSocketService.send(webSocketData, new LogResult(logOrigin));
                }
            }
            webSocketData.setBlockNum(num);
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            hashOperations.put(redisKeyConfig.getPushData(), webSocketData.getRequestHash(), JSON.toJSONString(webSocketData));
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

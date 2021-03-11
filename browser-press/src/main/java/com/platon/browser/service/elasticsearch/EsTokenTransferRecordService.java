package com.platon.browser.service.elasticsearch;

import com.platon.browser.elasticsearch.TokenTransferRecordESRepository;
import com.platon.browser.elasticsearch.dto.ESTokenTransferRecord;
import com.platon.browser.queue.handler.StageCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Service
public class EsTokenTransferRecordService extends EsService<ESTokenTransferRecord> {

    @Autowired
    private TokenTransferRecordESRepository tokenTransferRecordESRepository;

    @PostConstruct
    public void init() throws IOException {
        if (!tokenTransferRecordESRepository.existsIndex()) {
            Map<String, Object> setting = new HashMap(3);
            // 查询的返回数量，默认是10000
            setting.put("max_result_window", 2000000000);
            // 主碎片的数量
            setting.put("number_of_shards", 5);
            // 副本每个主碎片的数量
            setting.put("number_of_replicas", 1);
            tokenTransferRecordESRepository.createIndex(setting, null);
        }
    }

    @Override
    @Retryable(value = Exception.class, maxAttempts = Integer.MAX_VALUE)
    public void save(StageCache<ESTokenTransferRecord> stage) throws IOException, InterruptedException {
        try {
            Set<ESTokenTransferRecord> data = stage.getData();
            if (data.isEmpty()) {
                return;
            }
            int size = data.size() / POOL_SIZE;
            Set<Map<String, ESTokenTransferRecord>> groups = new HashSet<>();
            Map<String, ESTokenTransferRecord> group = new HashMap<>();
            for (ESTokenTransferRecord record : data) {
                group.put(record.getHash(), record);
                if (group.size() >= size) {
                    groups.add(group);
                    group = new HashMap<>();
                }
            }
            if (group.size() > 0) {
                groups.add(group);
            }
            CountDownLatch latch = new CountDownLatch(groups.size());
            for (Map<String, ESTokenTransferRecord> g : groups) {
                try {
                    tokenTransferRecordESRepository.bulkAddOrUpdate(g);
                } finally {
                    latch.countDown();
                }
            }
            latch.await();
        } catch (Exception e) {
            log.error("", e);
            throw e;
        }
    }

}

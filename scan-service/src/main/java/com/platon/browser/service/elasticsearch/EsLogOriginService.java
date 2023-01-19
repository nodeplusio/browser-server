package com.platon.browser.service.elasticsearch;

import com.platon.browser.elasticsearch.dto.LogOrigin;
import com.platon.browser.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Auther: Chendongming
 * @Date: 2019/10/25 15:12
 * @Description: ES服务
 */
@Slf4j
@Service
public class EsLogOriginService implements EsService<LogOrigin> {
    @Resource
    private EsLogOriginRepository esLogOriginRepository;

    @Retryable(value = BusinessException.class, maxAttempts = Integer.MAX_VALUE)
    public void save(Set<LogOrigin> logOrigins) throws IOException {
        if (logOrigins.isEmpty()) return;
        try {
            Map<String, LogOrigin> map = new HashMap<>();
            logOrigins.forEach(b -> map.put(b.getTransactionHash() + b.getLogIndex(), b));
            esLogOriginRepository.bulkAddOrUpdate(map);
        } catch (Exception e) {
            log.error("保存log原始数据出错", e);
            throw new BusinessException(e.getMessage());
        }
    }
}

package com.platon.browser.service.elasticsearch;

import com.platon.browser.elasticsearch.dto.TransactionOrigin;
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
public class EsTransactionOriginService implements EsService<TransactionOrigin> {

    @Resource
    private EsTransactionOriginRepository esTransactionOriginRepository;

    @Override
    @Retryable(value = BusinessException.class, maxAttempts = Integer.MAX_VALUE)
    public void save(Set<TransactionOrigin> transactions) throws IOException {
        if (transactions.isEmpty()) {
            return;
        }
        try {
            Map<String, TransactionOrigin> transactionMap = new HashMap<>();
            // 使用交易Hash作ES的docId
            transactions.forEach(t -> transactionMap.put(t.getHash(), t));
            esTransactionOriginRepository.bulkAddOrUpdate(transactionMap);
        } catch (Exception e) {
            log.error("交易原始数据存入es失败", e);
            throw new BusinessException(e.getMessage());
        }
    }

}

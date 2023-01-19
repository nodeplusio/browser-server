package com.platon.browser.service.elasticsearch;

import com.platon.browser.client.PlatOnClient;
import com.platon.browser.elasticsearch.dto.Transaction;
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
import java.util.stream.Collectors;

/**
 * @Auther: Chendongming
 * @Date: 2019/10/25 15:12
 * @Description: ES服务
 */
@Slf4j
@Service
public class EsTransactionService implements EsService<Transaction> {

    @Resource
    private EsTransactionRepository ESTransactionRepository;
    @Resource
    private EsTransactionOriginService esTransactionOriginService;
    @Resource
    private PlatOnClient platOnClient;

    @Override
    @Retryable(value = BusinessException.class, maxAttempts = Integer.MAX_VALUE)
    public void save(Set<Transaction> transactions) throws IOException {
        if (transactions.isEmpty()) {
            return;
        }
        try {
            Map<String, Transaction> transactionMap = new HashMap<>();
            // 使用交易Hash作ES的docId
            transactions.forEach(t -> transactionMap.put(t.getHash(), t));
            ESTransactionRepository.bulkAddOrUpdate(transactionMap);
            saveOrigin(transactions);
        } catch (Exception e) {
            log.error("", e);
            throw new BusinessException(e.getMessage());
        }
    }

    private void saveOrigin(Set<Transaction> transactions) throws IOException {
        esTransactionOriginService.save(transactions.stream().map(this::getOrigin).collect(Collectors.toSet()));
    }

    private TransactionOrigin getOrigin(Transaction transaction) {
        try {
            com.platon.protocol.core.methods.response.Transaction transactionOrigin = platOnClient.getWeb3jWrapper().getWeb3j()
                    .platonGetTransactionByHash(transaction.getHash())
                    .send().getTransaction()
                    .orElseThrow(() -> new RuntimeException(String.format("交易%s没有查询到", transaction.getHash())));
            return new TransactionOrigin(transactionOrigin);
        } catch (Throwable e) {
            log.error("查询交易原始数据失败", e);
            throw new RuntimeException(e);
        }
    }

}

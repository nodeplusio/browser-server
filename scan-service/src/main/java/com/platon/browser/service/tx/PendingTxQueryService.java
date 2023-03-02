package com.platon.browser.service.tx;

import com.platon.browser.client.PlatOnClient;
import com.platon.protocol.admin.methods.response.TxPoolContent;
import com.platon.protocol.core.Request;
import com.platon.protocol.core.methods.response.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PendingTxQueryService {

    @Resource
    private PlatOnClient platOnClient;

    public List<Transaction> queryPendingTxList() {
        try {
            Request<?, TxPoolContent> txPoolContent = platOnClient.getWeb3jWrapper().getAdmin().txPoolContent();
            TxPoolContent.TxPoolContentResult result = txPoolContent.send().getResult();
            Map<String, Map<BigInteger, Transaction>> resultPending = result.getPending();
            return resultPending.values()
                    .stream()
                    .map(Map::values)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("查询txPoolContent出错", e);
        }
        return new ArrayList<>();
    }
}

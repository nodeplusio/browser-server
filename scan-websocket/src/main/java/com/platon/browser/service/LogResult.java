package com.platon.browser.service;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.platon.browser.elasticsearch.dto.LogOrigin;
import com.platon.browser.util.BigIntegerToHexSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Data
@NoArgsConstructor
public class LogResult {
    private boolean removed;
    @JsonSerialize(using = BigIntegerToHexSerializer.class)
    private BigInteger logIndex;
    @JsonSerialize(using = BigIntegerToHexSerializer.class)
    private BigInteger transactionIndex;
    private String transactionHash;
    private String blockHash;
    @JsonSerialize(using = BigIntegerToHexSerializer.class)
    private BigInteger blockNumber;
    private String address;
    private String data;
    private List<String> topics;

    public LogResult(LogOrigin log) {
        this.removed = log.isRemoved();
        this.logIndex = log.getLogIndex();
        this.transactionIndex = log.getTransactionIndex();
        this.transactionHash = log.getTransactionHash();
        this.blockHash = log.getBlockHash();
        this.removed = log.isRemoved();
        this.blockNumber = log.getBlockNumber();
        this.address = log.getAddress();
        this.data = log.getData();
        this.topics = log.getTopics();
    }
}

package com.platon.browser.service;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.platon.browser.util.AddressLatToHexSerializer;
import com.platon.protocol.core.methods.response.Log;
import lombok.Data;

import java.util.List;

@Data
public class LogResult {
    private boolean removed;
    private String logIndex;
    private String transactionIndex;
    private String transactionHash;
    private String blockHash;
    private String blockNumber;
    @JsonSerialize(using = AddressLatToHexSerializer.class)
    private String address;
    private String data;
    private List<String> topics;

    public LogResult(Log log) {
        this.removed = log.isRemoved();
        this.logIndex = log.getLogIndexRaw();
        this.transactionIndex = log.getTransactionIndexRaw();
        this.transactionHash = log.getTransactionHash();
        this.blockHash = log.getBlockHash();
        this.removed = log.isRemoved();
        this.blockNumber = log.getBlockNumberRaw();
        this.address = log.getAddress();
        this.data = log.getData();
        this.topics = log.getTopics();
    }
}

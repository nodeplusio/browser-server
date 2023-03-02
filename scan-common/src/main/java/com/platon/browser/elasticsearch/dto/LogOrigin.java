package com.platon.browser.elasticsearch.dto;

import com.platon.bech32.Bech32;
import com.platon.parameters.NetworkParameters;
import com.platon.protocol.core.methods.response.Log;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Data
@NoArgsConstructor
public class LogOrigin {
    private boolean removed;
    private BigInteger logIndex;
    private BigInteger transactionIndex;
    private String transactionHash;
    private String blockHash;
    private BigInteger blockNumber;
    private String address;
    private String data;
    private String type;
    private List<String> topics;

    public LogOrigin(Log log) {
        removed = log.isRemoved();
        if (log.getLogIndexRaw() != null) {
            logIndex = log.getLogIndex();
        }
        if (log.getTransactionIndexRaw() != null) {
            transactionIndex = log.getTransactionIndex();
        }
        transactionHash = log.getTransactionHash();
        blockHash = log.getBlockHash();
        if (log.getBlockNumberRaw() != null) {
            blockNumber = log.getBlockNumber();
        }
        NetworkParameters.selectPlatON();
        this.address =  Bech32.addressDecodeHex(log.getAddress());
        data = log.getData();
        type = log.getType();
        topics = log.getTopics();
    }
}

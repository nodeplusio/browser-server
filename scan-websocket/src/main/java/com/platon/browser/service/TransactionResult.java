package com.platon.browser.service;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.platon.browser.elasticsearch.dto.TransactionOrigin;
import com.platon.browser.util.BigIntegerToHexSerializer;
import com.platon.browser.util.LongToHexSerializer;
import com.platon.protocol.core.methods.response.Transaction;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.math.BigInteger;

@Data
@NoArgsConstructor
public class TransactionResult {
    private String hash;
    private String nonce;
    private String blockHash;
    @JsonSerialize(using = BigIntegerToHexSerializer.class)
    private BigInteger blockNumber;
    private String transactionIndex;
    private String from;
    private String to;
    @JsonSerialize(using = BigIntegerToHexSerializer.class)
    private BigInteger value;
    @JsonSerialize(using = BigIntegerToHexSerializer.class)
    private BigInteger gasPrice;
    @JsonSerialize(using = BigIntegerToHexSerializer.class)
    private BigInteger gas;
    private String input;
    private String r;
    private String s;
    @JsonSerialize(using = LongToHexSerializer.class)
    private Long v;
    @JsonSerialize(using = LongToHexSerializer.class)
    private Long chainId;
    private String type = "0x0";

    public TransactionResult(TransactionOrigin transaction) {
        BeanUtils.copyProperties(transaction, this);
    }

    public TransactionResult(Transaction transaction) {
        hash = transaction.getHash();
        nonce = transaction.getNonceRaw();
        blockHash = transaction.getBlockHash();
        blockNumber = transaction.getBlockNumber();
        transactionIndex = transaction.getTransactionIndexRaw();
        from = transaction.getFrom();
        to = transaction.getTo();
        value = transaction.getValue();
        gasPrice = transaction.getGasPrice();
        gas = transaction.getGas();
        input = transaction.getInput();
        r = transaction.getR();
        s = transaction.getS();
        v = transaction.getV();
        chainId = transaction.getChainId();
    }
}

package com.platon.browser.service;

import com.platon.protocol.core.methods.response.Transaction;
import com.platon.utils.Numeric;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
public class TransactionResult {
    private String hash;
    private String nonce;
    private String blockHash;
    private String blockNumber;
    private String transactionIndex;
    private String from;
    private String to;
    private String value;
    private String gasPrice;
    private String gas;
    private String input;
    private String r;
    private String s;
    private String v;
    private String chainId;
    private String type = "0x0";

    public TransactionResult(Transaction transaction) {
        hash = transaction.getHash();
        nonce = transaction.getNonceRaw();
        blockHash = transaction.getBlockHash();
        blockNumber = transaction.getBlockNumberRaw();
        transactionIndex = transaction.getTransactionIndexRaw();
        from = transaction.getFrom();
        to = transaction.getTo();
        value = transaction.getValueRaw();
        gasPrice = transaction.getGasPriceRaw();
        gas = transaction.getGasRaw();
        input = transaction.getInput();
        r = transaction.getR();
        s = transaction.getS();
        v = Numeric.encodeQuantity(BigInteger.valueOf(transaction.getV()));
        chainId = Numeric.encodeQuantity(BigInteger.valueOf(transaction.getChainId()));
    }
}

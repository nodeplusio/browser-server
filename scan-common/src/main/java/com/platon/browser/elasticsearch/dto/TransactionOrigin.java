package com.platon.browser.elasticsearch.dto;

import com.platon.bech32.Bech32;
import com.platon.parameters.NetworkParameters;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
public class TransactionOrigin {
    private String hash;
    private String nonce;
    private String blockHash;
    private BigInteger blockNumber;
    private BigInteger transactionIndex;
    private String from;
    private String to;
    private BigInteger value;
    private BigInteger gasPrice;
    private BigInteger gas;
    private String input;
    private String creates;
    private String publicKey;
    private String raw;
    private String r;
    private String s;
    private long v;
    private Long chainId;

    public TransactionOrigin(com.platon.protocol.core.methods.response.Transaction transaction) {
        this.hash = transaction.getHash();
        this.nonce = transaction.getNonceRaw();
        this.blockHash = transaction.getBlockHash();
        if (transaction.getBlockNumberRaw() != null) {
            this.blockNumber = transaction.getBlockNumber();
        }
        if (transaction.getTransactionIndexRaw() != null) {
            this.transactionIndex = transaction.getTransactionIndex();
        }
        NetworkParameters.selectPlatON();
        this.from =  Bech32.addressDecodeHex(transaction.getFrom());
        this.to =  Bech32.addressDecodeHex(transaction.getTo());
        if (transaction.getValueRaw() != null) {
            this.value = transaction.getValue();
        }
        if (transaction.getGasPriceRaw() != null) {
            this.gasPrice = transaction.getGasPrice();
        }
        if (transaction.getGasRaw() != null) {
            this.gas = transaction.getGas();
        }
        this.input = transaction.getInput();
        this.creates = transaction.getCreates();
        this.publicKey = transaction.getPublicKey();
        this.raw = transaction.getRaw();
        this.r = transaction.getR();
        this.s = transaction.getS();
        this.v = transaction.getV();
        this.chainId = transaction.getChainId();
    }
}
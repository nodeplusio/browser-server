package com.platon.browser.response.gas;

import com.platon.protocol.core.methods.response.Transaction;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigInteger;

@Data
public class PendingTxInfoResp {

    private String hash;
    private BigInteger value;
    private BigInteger gasPrice;
    private BigInteger gas;
    private String from;
    private String to;

    public PendingTxInfoResp(Transaction transaction) {
        BeanUtils.copyProperties(transaction, this);
        value = transaction.getValue();
        gasPrice = transaction.getGasPrice();
        gas = transaction.getGas();
    }
}

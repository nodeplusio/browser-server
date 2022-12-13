package com.platon.browser.response.staking;

import com.platon.browser.dao.entity.TxBakWithBLOBs;
import com.platon.browser.elasticsearch.dto.Transaction;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Data
public class TransactionDetail {
    private String input;

    private String info;

    private String erc1155TxInfo;

    private String erc721TxInfo;

    private String erc20TxInfo;

    private String transferTxInfo;

    private String pposTxInfo;

    private String failReason;

    private String method;

    private Long id;
    private String hash;
    private String bHash;
    private Long num;
    private Integer index;
    private Date time;
    private String nonce;
    private Integer status;
    private String gasPrice;
    private String gasUsed;
    private String gasLimit;
    private String from;
    private String to;
    private String value;
    private Integer type;
    private String cost;
    private Integer toType;
    private Long seq;
    private Date creTime;
    private Date updTime;
    private Integer contractType;
    private String contractAddress;

    public TransactionDetail(TxBakWithBLOBs t) {
        BeanUtils.copyProperties(t, this);
    }

    public TransactionDetail(Transaction t) {
        BeanUtils.copyProperties(t, this);
    }
}
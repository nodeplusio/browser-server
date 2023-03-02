package com.platon.browser.response.address;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.platon.browser.config.json.CustomLatSerializer;
import com.platon.browser.dao.entity.Address;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 查询地址的返回的对象
 *
 * @author zhangrj
 * @file QueryDetailResp.java
 * @description
 * @data 2019年8月31日
 */
@Data
public class AddressValueAllCols {

    /**
     * 地址
     */
    private String address;

    /**
     * 地址类型 :1账号,2内置合约 ,3EVM合约,4WASM合约
     */
    private Integer type;

    /**
     * 余额(单位:VON)
     */
    private BigDecimal balance;

    /**
     * 锁仓余额(单位:VON)
     */
    private BigDecimal restrictingBalance;

    /**
     * 质押的金额
     */
    private BigDecimal stakingValue;

    /**
     * 委托的金额
     */
    private BigDecimal delegateValue;

    /**
     * 赎回中的金额
     */
    private BigDecimal redeemedValue;

    /**
     * 交易总数
     */
    private Integer txQty;

    /**
     * token erc20交易总数
     */
    private Integer tokenQty;

    /**
     * 转账交易总数
     */
    private Integer transferQty;

    /**
     * 委托交易总数
     */
    private Integer delegateQty;

    /**
     * 验证人交易总数
     */
    private Integer stakingQty;

    /**
     * 治理交易总数
     */
    private Integer proposalQty;

    /**
     * 已委托验证人
     */
    private Integer candidateCount;

    /**
     * 未锁定委托（VON）
     */
    private BigDecimal delegateHes;

    /**
     * 已锁定委托（VON）
     */
    private BigDecimal delegateLocked;

    /**
     * 待赎回委托（VON）
     */
    private BigDecimal delegateReleased;

    /**
     * 已提取委托（VON）
     */
    private BigDecimal haveReward;

    /**
     * 余额+质押的金额+委托的金额(单位:LAT)
     */
    @JsonSerialize(using = CustomLatSerializer.class)
    private BigDecimal totalValue;

    /**
     * 合约名称
     */
    private String contractName;

    /**
     * 合约创建者地址
     */
    private String contractCreate;

    /**
     * 合约创建哈希
     */
    private String contractCreatehash;

    /**
     * 销毁合约的交易Hash
     */
    private String contractDestroyHash;

    /**
     * 合约bin
     */
    private String contractBin;


    public AddressValueAllCols(Address address) {
        this.address = address.getAddress();
        this.type = address.getType();
        this.restrictingBalance = address.getRestrictingBalance();
        this.balance = address.getBalance();
        this.stakingValue = address.getStakingValue();
        this.delegateValue = address.getDelegateValue();
        this.redeemedValue = address.getRedeemedValue();
        this.txQty = address.getTxQty();
        this.tokenQty = address.getErc20TxQty();
        this.transferQty = address.getTransferQty();
        this.delegateQty = address.getDelegateQty();
        this.stakingQty = address.getStakingQty();
        this.proposalQty = address.getProposalQty();
        this.candidateCount = address.getCandidateCount();
        this.delegateHes = address.getDelegateHes();
        this.delegateLocked = address.getDelegateLocked();
        this.delegateReleased = address.getDelegateReleased();
        this.contractName = address.getContractName();
        this.contractCreate = address.getContractCreate();
        this.contractCreatehash = address.getContractCreatehash();
        this.contractDestroyHash = address.getContractDestroyHash();
        this.contractBin = address.getContractBin();
        this.haveReward = address.getHaveReward();
        this.totalValue = address.getBalance().add(address.getStakingValue()).add(address.getDelegateValue());
    }

}

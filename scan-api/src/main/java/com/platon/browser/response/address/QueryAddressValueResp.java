package com.platon.browser.response.address;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.platon.browser.config.json.CustomLatSerializer;
import com.platon.browser.dao.entity.Address;
import com.platon.browser.dao.entity.RpPlan;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 查询地址锁仓信息的返回的对象
 */
@Data
public class QueryAddressValueResp {

    /**
     * 地址
     */
    private String address;

    /**
     * 余额(单位:VON)
     */
    private BigDecimal balance;

    /**
     * 质押的金额(单位:VON)
     */
    private BigDecimal stakingValue;

    /**
     * 委托的金额(单位:VON)
     */
    private BigDecimal delegateValue;

    /**
     * 余额+质押的金额+委托的金额(单位:LAT)
     */
    @JsonSerialize(using = CustomLatSerializer.class)
    private BigDecimal totalValue;

    private List<AddressValueRPPlan> RPPlan;

    public QueryAddressValueResp(Address address, List<RpPlan> rpPlans) {
        this.address = address.getAddress();
        this.balance = address.getBalance();
        this.stakingValue = address.getStakingValue();
        this.delegateValue = address.getDelegateValue();
        this.totalValue = address.getBalance().add(address.getDelegateValue()).add(address.getStakingValue());
        this.RPPlan = rpPlans.stream().map(AddressValueRPPlan::new).collect(Collectors.toList());
    }
}

package com.platon.browser.response.address;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.platon.browser.bean.CustomRpPlanStats;
import com.platon.browser.config.json.CustomLatSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 每日锁仓解锁的所有地址总金额列表的返回的对象
 *
 */
@Data
public class QueryAllEpochResp {

    /**
     * 总金额
     */
    @JsonSerialize(using = CustomLatSerializer.class)
    private BigDecimal totalAmount;

    private Long totalCount;

    private BigInteger estimateTime;

    private Long unlockNumber;

    public QueryAllEpochResp(CustomRpPlanStats t) {
        unlockNumber = t.getNumber();
        estimateTime = t.getEpoch();
        totalAmount = t.getTotalAmount();
        totalCount = t.getTotalCount();
    }
}

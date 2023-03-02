package com.platon.browser.response.address;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.platon.browser.config.json.CustomLatSerializer;
import com.platon.browser.dao.entity.RpPlan;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 每日锁仓解锁的所有地址总金额列表的返回的对象
 *
 */
@Data
public class QueryRpPlanByUnlockNumberResp {

    /**
     * 总金额
     */
    @JsonSerialize(using = CustomLatSerializer.class)
    private BigDecimal amount;

    private String address;

    public QueryRpPlanByUnlockNumberResp(RpPlan t) {
        amount = t.getAmount();
        address = t.getAddress();
    }
}

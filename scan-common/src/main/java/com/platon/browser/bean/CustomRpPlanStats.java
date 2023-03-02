package com.platon.browser.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @Description: 锁仓计划统计类
 */
@Data
public class CustomRpPlanStats {

    private Long number;

    private BigInteger epoch;

    private BigDecimal totalAmount;

    private Long totalCount;

}

package com.platon.browser.response.address;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.platon.browser.config.json.CustomLatSerializer;
import com.platon.browser.dao.entity.RpPlan;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *  地址详情锁仓子结构体返回对象
 */
@Data
public class AddressValueRPPlan {
	private BigInteger epoch;         //锁仓周期
	@JsonSerialize(using = CustomLatSerializer.class)
	private BigDecimal amount;      //锁定金额
    private Long blockNumber;   //锁仓周期对应快高  结束周期 * epoch

	public AddressValueRPPlan(RpPlan rpPlan) {
		this.epoch = rpPlan.getEpoch();
		this.amount = rpPlan.getAmount();
		this.blockNumber = rpPlan.getNumber();
	}
    
}
